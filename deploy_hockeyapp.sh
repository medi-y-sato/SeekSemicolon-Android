
GIT_COMPARE_KEY=${CIRCLE_COMPARE_URL##*/}
GIT_PRETTY_COMMIT_LOG=$(echo "<ul>$(git log ${GIT_COMPARE_KEY} --pretty=format:'<li>[%ad] %s (%an)</li>' --date=short)</ul>" | tr -d '\n')

HOCKEYAPP_NOTES_HEADER="**Built on:** $(date +"%a %d-%b-%Y %I:%M %p")
**Branch:** $(git rev-parse --abbrev-ref HEAD)
**Commit:** $(git rev-parse --short HEAD)"

HOCKEYAPP_NOTES_HEADER_HTML=${HOCKEYAPP_NOTES_HEADER//$'\n'/<br>}
HOCKEYAPP_NOTES="${HOCKEYAPP_NOTES_HEADER_HTML} ${GIT_PRETTY_COMMIT_LOG}"

echo ${HOCKEYAPP_NOTES}

curl \
-F "status=2" \
-F "notify=0" \
-F "notes=${HOCKEYAPP_NOTES}" \
-F "ipa=@app/build/outputs/apk/app-debug.apk" \
-H "X-HockeyAppToken: $HockeyAppToken" \
https://rink.hockeyapp.net/api/2/apps/$HOCKEYAPP_APP_ID/app_versions/upload
