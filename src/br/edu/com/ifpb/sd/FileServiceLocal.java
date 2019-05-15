package br.edu.com.ifpb.sd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class FileServiceLocal {

    private String pathToFile;

    public FileServiceLocal(String pathToFile) {
        this.pathToFile = pathToFile;

        if(!Files.exists(Paths.get(pathToFile))) {
            try {
                Files.createFile(Paths.get(pathToFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(Instant instant) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(pathToFile)));
            oos.writeObject(instant);
            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Instant read() {

        Instant instant = Instant.now();
        try {
            ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(pathToFile)));
            instant = (Instant) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return instant;
    }
}
