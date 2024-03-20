package org.chorser.service.impl;

import org.chorser.entity.maimai.Chart;
import org.chorser.entity.maimai.Song;
import org.chorser.service.IFunctionService;
import org.chorser.util.HttpBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.chorser.entity.common.GuessItem.*;


public class GuessGameServiceImpl extends IFunctionService {

    private Song currentSong;

    private Thread currentThread;

    private final List<Song> songs;

    private List<String> triggers=new ArrayList<>();

    private List<String> answers=new ArrayList<>();

    private static HashMap<Integer,String> guessMap=new HashMap<>();

    private final ReentrantLock reentrantLock=new ReentrantLock();

    private Random random=new Random();

    private final Logger log= LoggerFactory.getLogger(GuessGameServiceImpl.class);



    @Override
    public String response(String input, MessageCreateEvent event) {
        if(triggers.contains(input)){
            if(!startNewGame(event)){
                return "游戏正在进行中哦！";
            }
            return answers.get(random.nextInt(answers.size()));
        }
        return null;
    }

    public Boolean startNewGame(MessageCreateEvent event){
        if(currentSong !=null){
            return false;
        }
        reentrantLock.lock();
        try {
            if(currentSong !=null){
                return false;
            }
            currentSong=songs.get(random.nextInt(songs.size()));
            log.info("当前猜歌为:\n"+currentSong);

            currentThread=new Thread(()->{
                try {
                    HashSet<Integer> guessSequence = new LinkedHashSet<>();
//                    TODO 猜歌
                    while (guessSequence.size() <= INFO_AMOUNT) {
                        guessSequence.add(random.nextInt(AMOUNT));
                    }
                    for (int record : guessSequence) {
                        switch (record){
                            case RED_NOTER:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的红谱谱师是"+currentSong.getCharts().get(2).getCharter());
                                break;
                            }
                            case PURPLE_NOTER:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的紫谱谱师是"+currentSong.getCharts().get(3).getCharter());
                                break;
                            }
                            case TYPE:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的类型为"+currentSong.getType());
                                break;
                            }
                            case RED_DS:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的红谱定数为"+currentSong.getRedDS());
                                break;
                            }
                            case PURPLE_DS:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的紫谱定数为"+currentSong.getPurpleDS());
                                break;
                            }
                            case RED_CHARTER:{
                                Thread.sleep(1000);
                                Chart redCharts = currentSong.getCharts().get(2);
                                event.getChannel().sendMessage("这首歌的红谱有"
                                        +redCharts.getNotes().get(redCharts.getNotes().size()-1)+"个绝赞");

                                break;
                            }
                            case PURPLE_CHARTER:{
                                Thread.sleep(1000);
                                Chart redCharts = currentSong.getCharts().get(3);
                                event.getChannel().sendMessage("这首歌的紫谱有"
                                        +redCharts.getNotes().get(redCharts.getNotes().size()-1)+"个绝赞");

                                break;
                            }
                            case BPM:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌的BPM为"+currentSong.getBasicInfo().getBpm());
                                break;
                            }
                            case FROM:{
                                Thread.sleep(1000);
                                event.getChannel().sendMessage("这首歌来自"+currentSong.getBasicInfo().getFrom());
                                break;
                            }
                        }
                    }
                    Thread.sleep(5000);
                    File imagePart = getAbstractImagePart(currentSong.getId());
                    event.getChannel().sendMessage("这首歌的部分抽象画为",imagePart);
                    event.getChannel().sendMessage("这首歌的信息为:"+currentSong);
                    imagePart.delete();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    currentSong=null;
                }
            });
            currentThread.start();



        }finally {
            reentrantLock.unlock();
        }
        return true;

    }

    public File getAbstractImagePart(String id){
        InputStream responseStream = HttpBuilder.getResponseStream("https://download.fanyu.site/abstract/" + currentSong.getId() + "_1.png");
        try {
            System.out.println("InputStream:"+responseStream);
            BufferedImage bufferedImage = ImageIO.read(responseStream);
            System.out.println(bufferedImage.toString());
            BufferedImage subImage = bufferedImage.getSubimage(0, 0, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);
            File tempFile = File.createTempFile("-image", ".png");
            ImageIO.write(subImage, "png", tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public GuessGameServiceImpl(List<Song> songs) {
        this.songs = songs;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
