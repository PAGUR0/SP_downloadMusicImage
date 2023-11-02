import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Main {

    private static URL urlImage;
    private static URL urlMusic;
    private static String strImage;
    private static String strMusic;

    public static void main(String[] args) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src//file//inFile.txt"));
            try {
                String[] stringsImage = bufferedReader.readLine().split(" ");
                urlImage = new URL(stringsImage[0]);
                strImage = stringsImage[1];

                String[] stringsMusic = bufferedReader.readLine().split(" ");
                urlMusic = new URL(stringsMusic[0]);
                strMusic = stringsMusic[1];
            } finally {
                bufferedReader.close();
            }
        } catch (IOException exception){
            System.out.println(exception.getMessage());
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Запуск задач в многопотоке
        executor.execute(() -> {
            try {
                downloadImage(urlImage, strImage, "image");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        executor.execute(() -> {
            try {
                downloadMusic(urlMusic, strMusic, "music");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Завершение выполнения задач
        executor.shutdown();
    }

    public static void downloadImage(URL website, String str, String name) throws IOException{
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());

        FileOutputStream fos = new FileOutputStream(str + name + ".jpg");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    public static void downloadMusic(URL website, String str, String name) throws IOException{
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());

        FileOutputStream fos = new FileOutputStream(str + name + ".mp3");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        playMusic(str + name + ".mp3");
    }

    public static void playMusic(String str) {
        try (FileInputStream inputStream = new FileInputStream(str)) {
            try {
                Player player = new Player(inputStream);
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}