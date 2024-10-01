import mobi
import os
import shutil

def convert_mobi_to_epub(input_path, output_path):
    try:
        # Extract the mobi file
        tempdir, filepath = mobi.extract(input_path)

        # Check if the extracted file is already an epub
        if filepath.endswith('.epub'):
            # Move the file to the output path
            shutil.move(filepath, output_path)
        else:
            # If it's not an epub, we can't convert it directly
            # You might want to implement additional conversion steps here
            # For now, we'll just return False to indicate failure
            shutil.rmtree(tempdir)
            return False

        # Clean up the temporary directory
        shutil.rmtree(tempdir)
        return True
    except Exception as e:
        print(f"Error converting file: {str(e)}")
        return False