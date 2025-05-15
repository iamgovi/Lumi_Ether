package com.spidrox.mainapplication;
import com.spidrox.Websocket;


import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import com.spidrox.Hello2;



@QuarkusMain
public class MainApplication implements QuarkusApplication{
	
	@Inject 
	Websocket wb;
	
	@Inject
	Hello2 h2;
	
	
	public int run(String... args) throws Exception {
		System.out.println("Hello ");
		System.out.println(wb.hello());
		System.out.println(h2.hello());
		Quarkus.waitForExit();
		return 0;
	}
}
