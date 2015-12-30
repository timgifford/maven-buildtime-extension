File defaultFilename = new File( basedir, "output.csv" );
assert !defaultFilename.isFile(): "output.csv exists, but should NOT exist."

File notDefaultFilename = new File( basedir, "not-default-file.csv" );
assert notDefaultFilename.isFile()
