import ebooklib
from ebooklib import epub
from io import BytesIO
import tempfile
import os
import json

def edit_metadata(file_contents, title=None, authors=None, description=None):
    temp_file_path = None
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix='.epub') as temp_file:
            temp_file.write(file_contents)
            temp_file_path = temp_file.name

        book = epub.read_epub(temp_file_path)

        if title:
            book.set_title(title)

        if authors:
            if 'DC' not in book.metadata:
                book.metadata['DC'] = {}
            book.metadata['DC']['creator'] = []
            for author in authors.split(','):
                book.add_author(author.strip())

        if description:
            if 'DC' not in book.metadata:
                book.metadata['DC'] = {}
            book.add_metadata('DC', 'description', description)

        output = BytesIO()
        epub.write_epub(output, book)
        return output.getvalue()
    except Exception as e:
        print(f"Error editing metadata: {e}")
        raise
    finally:
        if temp_file_path and os.path.exists(temp_file_path):
            os.unlink(temp_file_path)

def get_metadata(file_contents):
    temp_file_path = None
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix='.epub') as temp_file:
            temp_file.write(file_contents)
            temp_file_path = temp_file.name

        book = epub.read_epub(temp_file_path)

        title = book.get_metadata('DC', 'title')
        title = title[0][0] if title else ''

        authors = book.get_metadata('DC', 'creator')
        authors = ', '.join([author[0] for author in authors]) if authors else ''

        description = book.get_metadata('DC', 'description')
        description = description[0][0] if description else ''

        metadata = {
            'title': title,
            'authors': authors,
            'description': description
        }

        return json.dumps(metadata)
    except Exception as e:
        print(f"Error getting metadata: {e}")
        raise
    finally:
        if temp_file_path and os.path.exists(temp_file_path):
            os.unlink(temp_file_path)