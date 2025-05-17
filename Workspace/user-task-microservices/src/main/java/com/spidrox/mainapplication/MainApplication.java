package com.spidrox.mainapplication;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class MainApplication implements QuarkusApplication{
	public int run(String... args) throws Exception {
		System.out.println("Hello World");
		Quarkus.waitForExit();
		return 0;
	}
}
