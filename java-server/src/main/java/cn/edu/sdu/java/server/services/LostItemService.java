package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.LostItem;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.LostItemRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import cn.edu.sdu.java.server.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LostItemService {
    private static final Logger log = LoggerFactory.getLogger(LostItemService.class);
    private final LostItemRepository lostItemRepository;

    public LostItemService(LostItemRepository lostItemRepository) {
        this.lostItemRepository = lostItemRepository;
    }

    private Map<String, Object> getMapFromLostItem(LostItem item) {
        Map<String, Object> map = new HashMap<>();
        if (item == null) return map;
        
        map.put("id", item.getId());
        map.put("itemName", item.getItemName());
        map.put("location", item.getLocation());
        map.put("foundDate", DateTimeTool.parseDateTime(item.getFoundDate()));
        map.put("status", item.getStatus());
        map.put("contact", item.getContact());
        return map;
    }

    public DataResponse getLostItemList(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        String status = dataRequest.getString("status");
        String startDate = dataRequest.getString("startDate");
        String endDate = dataRequest.getString("endDate");

        List<LostItem> itemList;
        if (status != null && !status.isEmpty()) {
            itemList = lostItemRepository.findByStatus(status);
        } else if (startDate != null && endDate != null) {
            Date start = DateTimeTool.formatDateTime(startDate, "yyyy-MM-dd");
            Date end = DateTimeTool.formatDateTime(endDate, "yyyy-MM-dd");
            itemList = lostItemRepository.findByFoundDateBetween(start, end);
        } else {
            itemList = lostItemRepository.findByKeyword(keyword);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (LostItem item : itemList) {
            dataList.add(getMapFromLostItem(item));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse lostItemSave(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map<String, Object> form = dataRequest.getData();

        LostItem item;
        if (id != null) {
            Optional<LostItem> op = lostItemRepository.findById(id);
            if (op.isPresent()) {
                item = op.get();
            } else {
                return CommonMethod.getReturnMessageError("物品不存在");
            }
        } else {
            item = new LostItem();
        }

        item.setItemName(MapUtils.getString(form, "itemName"));
        item.setLocation(MapUtils.getString(form, "location"));
        item.setFoundDate(DateTimeTool.formatDateTime(MapUtils.getString(form, "foundDate"), "yyyy-MM-dd"));
        item.setStatus(MapUtils.getString(form, "status"));
        item.setContact(MapUtils.getString(form, "contact"));

        lostItemRepository.save(item);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse lostItemDelete(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id != null) {
            Optional<LostItem> op = lostItemRepository.findById(id);
            if (op.isPresent()) {
                lostItemRepository.delete(op.get());
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse updateStatus(DataRequest dataRequest) {
        try {
            Integer id = dataRequest.getInteger("id");
            String status = dataRequest.getString("status");
            
            if (id == null || status == null) {
                return CommonMethod.getReturnMessageError("ID和状态不能为空");
            }
            
            Optional<LostItem> optionalItem = lostItemRepository.findById(id);
            if (optionalItem.isEmpty()) {
                return CommonMethod.getReturnMessageError("找不到指定的失物招领信息");
            }
            
            LostItem item = optionalItem.get();
            item.setStatus(status);
            lostItemRepository.save(item);
            
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("更新状态失败", e);
            return CommonMethod.getReturnMessageError("更新状态失败：" + e.getMessage());
        }
    }

    public DataResponse getLostItemInfo(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        if (id == null) {
            return CommonMethod.getReturnMessageError("物品ID不能为空");
        }
        
        Optional<LostItem> op = lostItemRepository.findById(id);
        if (!op.isPresent()) {
            return CommonMethod.getReturnMessageError("物品不存在");
        }
        
        return CommonMethod.getReturnData(getMapFromLostItem(op.get()));
    }
}