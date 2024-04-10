package org.chorser.service.impl;

import com.google.gson.Gson;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.chorser.service.IDiscordService;
import org.chorser.web.AuthenticationInterceptor;
import org.javacord.api.event.message.MessageCreateEvent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class DiscGPTServiceImpl extends IDiscordService {


    private final Gson gson=new Gson();

    private Retrofit defaultRetrofit;

    private OkHttpClient defaultClient;

    private final OpenAiService openAiService;

    public DiscGPTServiceImpl(String token){
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        defaultClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .proxy(proxy)
                .build();

        defaultRetrofit = new Retrofit.Builder()
                .client(defaultClient)
                .baseUrl("https://api.openai.com/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        OpenAiApi openAiApi = defaultRetrofit.create(OpenAiApi.class);
        openAiService=new OpenAiService(openAiApi);
    }


    @Override
    public String response(String input, MessageCreateEvent event) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Somebody once told me the world is gonna roll me")
                .model("gpt-3.5-turbo")
                        .echo(true)
                        .build();
        openAiService.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        return null;
    }


}
