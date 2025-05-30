package lunalib.backend.ui.versionchecker;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import lunalib.lunaSettings.LunaSettings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public final class UpdateInfo
{
    private final List<ModInfo> hasUpdate = new ArrayList<>();
    private final List<ModInfo> hasNoUpdate = new ArrayList<>();
    private final List<ModInfo> failedCheck = new ArrayList<>();
    private int numModsChecked = 0;
    private String ssUpdate = null, ssUpdateError = null;

    public Boolean isStarsectorAhead = false;

    void setSSUpdate(String latestVersion)
    {
        this.ssUpdate = latestVersion;
    }

    String getSSUpdate()
    {
        return ssUpdate;
    }

    void setFailedSSError(String ssUpdateError)
    {
        this.ssUpdateError = ssUpdateError;
    }

    String getFailedSSError()
    {
        return ssUpdateError;
    }

    void addFailed(ModInfo mod)
    {
        failedCheck.add(mod);
        numModsChecked++;
    }

    List<ModInfo> getFailed()
    {
        return new ArrayList<>(failedCheck);
    }

    void addUpdate(ModInfo mod)
    {
        hasUpdate.add(mod);
        numModsChecked++;
    }

    List<ModInfo> getHasUpdate()
    {
        return new ArrayList<>(hasUpdate);
    }

    void addNoUpdate(ModInfo mod)
    {
        hasNoUpdate.add(mod);
        numModsChecked++;
    }

    int getNumModsChecked()
    {
        return numModsChecked;
    }

    List<ModInfo> getHasNoUpdate()
    {
        return new ArrayList<>(hasNoUpdate);
    }

    public static final class ModInfo implements Comparable<ModInfo>
    {
        private final VersionFile localVersion, remoteVersion;
        private final String errorMessage;

        ModInfo(VersionFile localVersion, VersionFile remoteVersion)
        {
            this.localVersion = localVersion;
            this.remoteVersion = remoteVersion;
            errorMessage = null;
        }

        ModInfo(VersionFile localVersion, String errorMessage)
        {
            this.localVersion = localVersion;
            this.errorMessage = errorMessage;
            remoteVersion = null;
        }

        String getName()
        {
            return localVersion.getName();
        }

        VersionFile getLocalVersion()
        {
            return localVersion;
        }

        VersionFile getRemoteVersion()
        {
            return remoteVersion;
        }

        boolean failedUpdateCheck()
        {
            return (remoteVersion == null);
        }

        String getErrorMessage()
        {
            return errorMessage;
        }

        boolean isUpdateAvailable()
        {
            return localVersion.isOlderThan(remoteVersion);
        }

        boolean isLocalNewer()
        {
            return localVersion.isNewerThan(remoteVersion);
        }

        String getVersionString()
        {
            if (remoteVersion == null || localVersion.isSameAs(remoteVersion))
            {
                return localVersion.getVersion();
            }

            return localVersion.getVersion() + " vs " + remoteVersion.getVersion();
        }

        @Override
        public int compareTo(ModInfo other)
        {
            return localVersion.getName().compareTo(other.localVersion.getName());
        }
    }

    public static final class VersionFile implements Comparable<VersionFile>
    {
        private static final String MOD_THREAD_FORMAT
                = "http://fractalsoftworks.com/forum/index.php?topic=%d.0";
        private static final String MOD_NEXUS_FORMAT
                = "https://www.nexusmods.com/starsector/mods/%d?tab=files";
        private int major, minor, modThreadId, modNexusId;
        private String patch, masterURL, modName;

        private String  directDownloadURL, changelogURL, txtChangelog;

        private String  githubOwner, githubRepo;

        private String modID = null;

        private Boolean enableAutomaticChangelog = null;
        private Boolean enableAutomaticVersioning = null;

        /*private String githubOwner, githubRepo;
        private Map<String, String> githubChangelog = new HashMap<>();*/

        VersionFile(final JSONObject json, boolean isMaster) throws JSONException
        {
            // Parse mod details (local version file only)
            masterURL = (isMaster ? null : json.getString("masterVersionFile"));
            modName = (isMaster ? null : json.optString("modName", "<unknown>"));
            modThreadId = (isMaster ? 0 : (int) json.optDouble("modThreadId", 0));
            modNexusId = (isMaster ? 0 : (int) json.optDouble("modNexusId", 0));


            githubOwner = json.optString("githubOwner");
            if (githubOwner.isEmpty()) githubOwner = null;

            githubRepo = json.optString("githubRepo");
            if (githubRepo.isEmpty()) githubRepo = null;

            /*modID = json.optString("modId");
            enableAutomaticChangelog = json.optBoolean("automaticGithubChangelog");
            enableAutomaticVersioning = json.optBoolean("automaticGithubVersioning");

            boolean readChangelogFromGithub = false;
            boolean readVersionFromGithub = false;
            if (githubOwner != null && githubRepo != null && isMaster) {
                String apiURL = "https://api.github.com/repos/" + githubOwner + "/" + githubRepo +"/releases";

                try {
                    InputStream stream = new URL(apiURL).openStream();
                    Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
                    String apiString = scanner.next();
                    JSONArray releases = new JSONArray(apiString);


                    if (enableAutomaticVersioning) {
                        JSONObject latest = releases.getJSONObject(0);
                        String tag = latest.getString("tag_name");
                        List<String> versions = Arrays.asList(tag.split("\\."));

                        major = Integer.parseInt(versions.get(0));
                        minor = Integer.parseInt(versions.get(1));
                        patch = versions.get(2);

                        readVersionFromGithub = true;
                    }

                    if (enableAutomaticChangelog) {
                        String changelog = "";

                        for (int i = 0; i < releases.length(); i++) {
                            JSONObject release = releases.getJSONObject(i);

                            String tag = release.getString("tag_name");
                            String date = release.getString("created_at");
                            int index = date.indexOf("T");
                            date = date.substring(0, index);

                            String[] times = date.split("-");
                            List<String> reversed = Arrays.asList(times);
                            Collections.reverse(reversed);
                            String combined = "";

                            for (int j = 0; j < reversed.size(); j++) {
                                String item = reversed.get(j);
                                combined += item;
                                if (j != 2) {
                                    combined += ".";
                                }

                            }

                            date = combined;

                            String body = release.getString("body");
                            body = body.replaceAll("\r", "");

                            changelog += "Version " + tag + "\n";
                            changelog += "Released: " + date + "\n\n";
                            changelog += body;

                            changelog += "\n\n\n";

                        }

                        txtChangelog = changelog;

                        readChangelogFromGithub = true;
                    }
                }
                catch (Throwable ex)
                {
                    Log.error("Error while loading github api from URL: \"" + apiURL + "\", Exception: " + ex.getClass());
                }
            }*/


            directDownloadURL = json.optString("directDownloadURL");
            if (directDownloadURL.equals("")) directDownloadURL = null;

            changelogURL = json.optString("changelogURL");
            if (changelogURL.equals("")) changelogURL = null;

            if (changelogURL != null && isMaster /*&& !readChangelogFromGithub*/)
            {
                try
                {
                    URLConnection connection = new URL(changelogURL).openConnection();
                    connection.setUseCaches(false);
                    InputStream stream = connection.getInputStream();
                    Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
                    txtChangelog = scanner.next();
                }
                //Temporarily removed stacktrace reporting for now, made it a lot harder to detect the real issues
                catch (Throwable ex)
                {
                    Log.error("Error while loading changelog from URL: \"" + txtChangelog + "\", Exception: " + ex.getClass());
                }
            }

            JSONObject modVersion = json.getJSONObject("modVersion");
            major = modVersion.optInt("major", 0);
            minor = modVersion.optInt("minor", 0);
            patch = modVersion.optString("patch", "0");

           /* if (!isMaster && enableAutomaticVersioning) {
                ModSpecAPI mod = Global.getSettings().getModManager().getModSpec(modID);

                List<String> versions = Arrays.asList(mod.getVersion().split("\\."));

                major = Integer.parseInt(versions.get(0));
                minor = Integer.parseInt(versions.get(1));
                patch = versions.get(2);
            }
            else if (!readVersionFromGithub) {
                // Parse version number
                JSONObject modVersion = json.getJSONObject("modVersion");
                major = modVersion.optInt("major", 0);
                minor = modVersion.optInt("minor", 0);
                patch = modVersion.optString("patch", "0");
            }*/



        }

        boolean isSameAs(VersionFile other)
        {
            return (compareTo(other) == 0);
        }

        boolean isOlderThan(VersionFile other)
        {
            return (compareTo(other) < 0);
        }

        boolean isNewerThan(VersionFile other)
        {
            return (compareTo(other) > 0);
        }

        String getName()
        {
            return modName;
        }

        private static boolean startsWithDigit(String str)
        {
            return (!str.isEmpty() && Character.isDigit(str.charAt(0)));
        }

        String getVersion()
        {
            // Don't show patch number if there isn't one
            if (patch.equals("0"))
            {
                return "v" + major + "." + minor;
            }

            // Support for character patch notation (v2.4b vs v2.4.1)
            if (startsWithDigit(patch))
            {
                return "v" + major + "." + minor + "." + patch;
            }
            else
            {
                return "v" + major + "." + minor + patch;
            }
        }

        String getMasterURL()
        {
            return masterURL;
        }

        String getDirectDownloadURL()
        {
            return directDownloadURL;
        }

        String getChangelogURL()
        {
            return changelogURL;
        }

        String getTxtChangelog()
        {
            return txtChangelog;
        }

        String getUpdateURL()
        {
            if (VCModPlugin.preferNexus)
            {
                return (modNexusId == 0 ? modThreadId == 0 ? null
                        : String.format(MOD_THREAD_FORMAT, modThreadId)
                        : String.format(MOD_NEXUS_FORMAT, modNexusId));
            }

            return (modThreadId == 0 ? modNexusId == 0 ? null
                    : String.format(MOD_NEXUS_FORMAT, modNexusId)
                    : String.format(MOD_THREAD_FORMAT, modThreadId));

        }

        @Override
        public String toString()
        {
            return getName() + " " + getVersion();
        }

        private static String[] splitPatch(String patch)
        {
            final StringBuilder digit = new StringBuilder(patch.length());
            String str = "";
            for (int i = 0; i < patch.length(); i++)
            {
                final char ch = patch.charAt(i);
                if (Character.isDigit(ch))
                {
                    digit.append(ch);
                }
                else
                {
                    str = patch.substring(i);
                    break;
                }
            }

            //System.out.println(digit + " | " + str);
            return new String[]
                    {
                            digit.toString(), str
                    };
        }

        private static int comparePatch(String patch, String other)
        {
            // Compare digits as digits, so v11 is considered newer than v9
            if (startsWithDigit(patch) && startsWithDigit(other))
            {
                final String[] subPatch = splitPatch(patch),
                        subOther = splitPatch(other);
                final int numPatch = Integer.parseInt(subPatch[0]),
                        numOther = Integer.parseInt(subOther[0]);

                // If digits are the same, compare any remaining characters
                if (numPatch == numOther)
                {
                    return subPatch[1].compareTo(subOther[1]);
                }

                return Integer.compare(numPatch, numOther);
            }

            return patch.compareToIgnoreCase(other);
        }

        @Override
        public int compareTo(VersionFile other)
        {
            if (other == null)
            {
                // Assume there's an update if something went wrong
                return -1;
            }

            if (major == other.major && minor == other.minor
                    && patch.equalsIgnoreCase(other.patch))
            {
                return 0;
            }

            if ((major < other.major) || (major == other.major && minor < other.minor)
                    || (major == other.major && minor == other.minor
                    && comparePatch(patch, other.patch) < 0))
            {
                return -1;
            }

            return 1;
        }
    }
}
