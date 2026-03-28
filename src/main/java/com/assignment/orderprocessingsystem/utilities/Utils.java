package com.assignment.orderprocessingsystem.utilities;

import com.assignment.orderprocessingsystem.dataObjects.ItemDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class Utils {

    private final String ORDER_PREFIX = "PI";

    public String generateReferenceNumber() {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMDDssmmhh"));
        return ORDER_PREFIX + timeStamp;
    }

    public Boolean isDuplicateEntries(List<ItemDTO> items){
        ArrayList<Long> itemIds = new ArrayList<>();
        for(ItemDTO item : items){
            if(itemIds.contains(item.getId())){
               return true;
            }
            else{
                itemIds.add(item.getId());
            }
        }
        return false;
    }
}
