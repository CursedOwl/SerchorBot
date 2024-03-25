package org.chorser.service.impl;

import org.chorser.entity.maimai.Chart;
import org.chorser.entity.maimai.Song;
import org.chorser.service.IFunctionService;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import static org.chorser.common.GuessItem.*;


public class GuessGameServiceImpl extends IFunctionService {

    private Song currentSong;

    private Thread currentThread;

    private final List<Song> songs;

    private final HashMap<Integer,List<String>> alias;

    private List<String> triggers=new ArrayList<>();

    private List<String> answers=new ArrayList<>();

    private final ReentrantLock reentrantLock=new ReentrantLock();

    private Random random=new Random();

    private final Logger log= LoggerFactory.getLogger(GuessGameServiceImpl.class);



    @Override
    public String response(String input, MessageCreateEvent event) {
        if(triggers.contains(input)){
            if(!startNewGame(event)){
                return "游戏正在进行中哦！";
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle(answers.get(random.nextInt(answers.size())))
                    .setColor(Color.white));
            return null;
        }
        return null;
    }

    public Boolean guessGame(MessageCreateEvent event,String content){

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
                    while (guessSequence.size() < INFO_AMOUNT) {
                        guessSequence.add(random.nextInt(AMOUNT));
                    }
                    for (int record : guessSequence) {
                        switch (record){
                            case RED_NOTER:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的红谱谱师是"+currentSong.getCharts().get(2).getCharter())
                                        .setColor(Color.white));
                                break;
                            }
                            case PURPLE_NOTER:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的紫谱谱师是"+currentSong.getCharts().get(3).getCharter())
                                        .setColor(Color.white));
                                break;
                            }
                            case TYPE:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的类型为"+currentSong.getType())
                                        .setColor(Color.white));
                                break;
                            }
                            case RED_DS:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的红谱定数为"+currentSong.getRedDS())
                                        .setColor(Color.white));
                                break;
                            }
                            case PURPLE_DS:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的紫谱定数为"+currentSong.getPurpleDS())
                                        .setColor(Color.white));
                                break;
                            }
                            case RED_CHARTER:{
                                Thread.sleep(WAIT_TIME);
                                Chart redCharts = currentSong.getCharts().get(2);
                                event.getChannel().sendMessage(new EmbedBuilder().setDescription("这首歌的红谱有"
                                        +redCharts.getNotes().get(redCharts.getNotes().size()-1)+"个绝赞")
                                        .setColor(Color.white));

                                break;
                            }
                            case PURPLE_CHARTER:{
                                Thread.sleep(WAIT_TIME);
                                Chart redCharts = currentSong.getCharts().get(3);
                                event.getChannel().sendMessage(new EmbedBuilder().setDescription("这首歌的紫谱有"
                                        +redCharts.getNotes().get(redCharts.getNotes().size()-1)+"个绝赞")
                                        .setColor(Color.white));

                                break;
                            }
                            case BPM:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌的BPM为"+currentSong.getBasicInfo().getBpm())
                                        .setColor(Color.white));
                                break;
                            }
                            case FROM:{
                                Thread.sleep(WAIT_TIME);
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setDescription("这首歌来自"+currentSong.getBasicInfo().getFrom())
                                        .setColor(Color.white));
                                break;
                            }
                        }
                    }
                    Thread.sleep(ABSTRACT_TIME);
                    File imagePart = getAbstractImagePart(currentSong.getId());
                    event.getChannel().sendMessage(new EmbedBuilder().setTitle("这首歌的抽象画如下").setColor(Color.WHITE));
                    CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(imagePart);
//                    等待异步线程同步之后再进行下一步工作
                    messageCompletableFuture.join();
                    imagePart.delete();
                    Thread.sleep(ANSWER_TIME);
                    event.getChannel().sendMessage(new EmbedBuilder().setTitle("这首歌的信息为")
                            .setDescription(currentSong.toString())
                            .setColor(Color.white));
                    event.getChannel().sendMessage(new EmbedBuilder().setTitle("这首歌的别名信息为")
                            .setDescription(alias.get(Integer.parseInt(currentSong.getId())).toString())
                            .setColor(Color.white));

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
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL("https://download.fanyu.site/abstract/" + currentSong.getId() + "_1.png"));
            int x=random.nextInt(bufferedImage.getWidth() / 4);
            int y=random.nextInt(bufferedImage.getHeight() / 4);
            BufferedImage subImage = bufferedImage.getSubimage(x, y, x+bufferedImage.getWidth() / 2, y+bufferedImage.getHeight() / 2);
            File tempFile = File.createTempFile("guessGame", ".png");
            ImageIO.write(subImage, "png", tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public GuessGameServiceImpl(List<Song> songs,HashMap<Integer,List<String>> alias) {
        this.songs = songs;
        this.alias=alias;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
