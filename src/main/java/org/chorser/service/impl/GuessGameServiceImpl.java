package org.chorser.service.impl;

import org.chorser.entity.maimai.Song;
import org.chorser.service.IFunctionService;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class GuessGameServiceImpl extends IFunctionService {

    private Song currentSong;

    private Thread currentThread;

    private final List<Song> songs;

    private List<String> triggers=new ArrayList<>();

    private List<String> answers=new ArrayList<>();

    private HashMap<Integer,String> guessMap=new HashMap<>();

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
                    event.getChannel().sendMessage("1");
                    Thread.sleep(5000);
                    event.getChannel().sendMessage("2");


                } catch (InterruptedException ignored) {
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
