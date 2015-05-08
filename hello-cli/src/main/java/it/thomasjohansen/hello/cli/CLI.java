package it.thomasjohansen.hello.cli;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.config.SpringConfig;
import it.thomasjohansen.hello.types.HelloRequest;
import it.thomasjohansen.hello.types.HelloResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;

public class CLI {

    private enum Operation {
        SayHello
    }

    private HelloPort service;

    public static void main(String[] args) throws IOException {
        new CLI();
    }

    public CLI() throws MalformedURLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        service = (HelloPort)context.getBean("webServiceClient");
        while (true) {
            try {
                provision();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void provision() {
        switch (getOperation()) {
            case SayHello:
                sayHello(CLIUtils.readInput("Greeting> "));
                return;
        }
    }

    private void sayHello(String greeting) {
        HelloRequest request = new HelloRequest();
        request.setGreeting(greeting);
        HelloResponse response = service.sayHello(request);
        System.out.println(response.getGreetingResponse());
    }

    private Operation getOperation() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Select operation:\n");
        int index = 0;
        for (Operation operation : Operation.values()) {
            prompt.append(" ").append(++index).append(". ").append(operation.toString()).append("\n");
        }
        prompt.append("Operation> ");
        return Operation.values()[CLIUtils.readInputInt(prompt.toString(), 1, index) - 1];
    }

}
