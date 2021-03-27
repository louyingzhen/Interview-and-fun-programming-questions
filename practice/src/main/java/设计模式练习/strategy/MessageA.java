package 设计模式练习.strategy;

import org.springframework.stereotype.Service;

@Service("MessageA")
public class MessageA implements Message {

    @Override
    public String sendMessage() {
        return "MessageA";
    }
}
