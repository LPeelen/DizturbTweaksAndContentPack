folderToZip=$1
outputFolderName=$2
startDir=$(pwd)

cd "$folderToZip" || return
zipName=$outputFolderName.zip

# Clean up
rm -rf "./$outputFolderName"
mkdir "$outputFolderName"

# Copy all files and directories that are not ignored by Git or blacklisted.
git ls-files | grep -Evf "$startDir"/blacklisted-files.txt | while read -r file; do cp --parents "$file" "$outputFolderName"; done

# Zip the folder, then clean it up.
zip -r "$zipName" "./$outputFolderName"
echo "Created zip file at $(realpath "$zipName")"
rm -rf "./$outputFolderName"

# Move the zip to the artifacts folder in the executing directory.
mkdir -p "$startDir"/artifacts
mv ./"$zipName" "$startDir"/artifacts/
echo "Moved zip file to $(realpath "$startDir"/artifacts/"$zipName")"

# Return to the executing directory.
cd "$startDir" || return