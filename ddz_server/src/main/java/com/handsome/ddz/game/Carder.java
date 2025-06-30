package com.handsome.ddz.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Carder {

    private List<Card> cardList;

    public List<List<Card>> splitThreeCards() {
        // 参数校验：牌列表至少需要54张（17 * 3 + 3）
        if (cardList.size() < 54) {
            throw new IllegalArgumentException("牌列表长度必须至少为54，当前长度：" + cardList.size());
        }

        // 初始化一个Map，用于存储三份牌（键0、1、2分别对应三份）
        Map<Integer, List<Card>> threeCards = new HashMap<>();

        // 循环17次，每次为三份牌各添加一张牌
        for (int i = 0; i < 17; i++) {
            // 内层循环3次，为三份牌依次添加当前牌
            for (int j = 0; j < 3; j++) {
                // 从牌列表末尾移除一张牌（模拟JavaScript的pop操作）
                Card currentCard = cardList.remove(cardList.size() - 1);
                // 如果当前份（j）不存在于Map中，创建一个新的空列表；否则直接获取已有列表
                threeCards.computeIfAbsent(j, k -> new ArrayList<>()).add(currentCard);
            }
        }

        // 构建结果列表：前三个元素是三份17张的牌，第四个元素是剩余的3张牌
        List<List<Card>> result = new ArrayList<>();
        result.add(threeCards.get(0));   // 第一份牌（17张）
        result.add(threeCards.get(1));   // 第二份牌（17张）
        result.add(threeCards.get(2));   // 第三份牌（17张）
        result.add(cardList);            // 剩余的3张牌（带翻）

        return result;
    }
}
