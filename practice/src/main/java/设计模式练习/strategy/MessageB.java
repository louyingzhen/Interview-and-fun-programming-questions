package 设计模式练习.strategy;

import org.springframework.stereotype.Service;

@Service("MessageA")
public class MessageB implements Message {

    @Override
    public String sendMessage() {
        return "MessageB";
    }
}
