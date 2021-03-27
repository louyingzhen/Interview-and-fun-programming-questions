package 设计模式练习.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class test {

    @Autowired Map<String, Message> messageStrategy;

    public void main(String[] args) {
        Message message = messageStrategy.get("testA");
        message.sendMessage();
    }
}
