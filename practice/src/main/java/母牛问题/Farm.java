package 母牛问题;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** 农场对象 */
@Data
@NoArgsConstructor
public class Farm {
    // 农场里所有牛的集合
    private List<Cow> cowList = new ArrayList<Cow>();
}
