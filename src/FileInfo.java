package src;

import java.io.*;

public class FileInfo {
    private File file;
    private FileReader fr;
    private BufferedReader br;

    public File getFile() { return file; }
    public void setFile(File f) { this.file = f; }

    public FileReader getFR() { return fr; }
    public void setFR(FileReader filer) { this.fr = filer; }

    public BufferedReader getBR() { return br; }
    public void setBR(BufferedReader buffer) { this.br = buffer;}

    public FileInfo(File f, FileReader filer, BufferedReader buffer){
        this.file = f;
        this.fr = filer;
        this.br = buffer;
    }
}
