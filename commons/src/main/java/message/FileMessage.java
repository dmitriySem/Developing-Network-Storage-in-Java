package message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends Message{

    private Path workDir;
    private Path currenPathDir;
    private byte[] content;
    private long startPosition;
    private String FileName;

    public FileMessage() {
        this.currenPathDir = Path.of(System.getProperty("user.dir"));
        try {
            if(!Files.isDirectory(currenPathDir)){
                workDir = Files.createDirectory(Path.of(currenPathDir + File.separator + "LocalRepository"));
                Files.setAttribute(workDir,"dos:hidden", true);
            } else {
                workDir = Path.of(currenPathDir + File.separator + "LocalRepository");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getStartPosition() { return startPosition; }
    public void setStartPosition(long startPosition) { this.startPosition = startPosition; }
    public Path getWorkDir() { return workDir; }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return FileName;
    }
    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
