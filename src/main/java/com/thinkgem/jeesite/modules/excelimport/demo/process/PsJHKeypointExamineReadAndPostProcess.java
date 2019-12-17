package com.thinkgem.jeesite.modules.excelimport.demo.process;

import com.thinkgem.jeesite.modules.excelimport.demo.depenency.PsJHKeypointExamine;
import com.thinkgem.jeesite.modules.excelimport.process.IReadAndPostProcess;
import com.thinkgem.jeesite.modules.excelimport.process.SingleExcelReadAndPost;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PsJHKeypointExamineReadAndPostProcess extends SingleExcelReadAndPost {

    protected ThreadLocal<PsJHKeypointExamine> head = new ThreadLocal<PsJHKeypointExamine>();


    @Override
    public synchronized IReadAndPostProcess initial(Object entity, String entityType) {
        this.setEntityType(entityType);
        if (this.strategyMap == null) {
            this.strategyMap = new HashMap(4);
            this.strategyMap.put(entityType + PsJHKeypointExamine.class.getSimpleName(), new PsJHKeyPointJustifyStrategy());
        }
        return this;
    }


    @Override
    public boolean createAndSaveObjects(List<Object> list) throws Exception {

        if (head.get() == null) {
            head.set(new PsJHKeypointExamine());
        }
        PsJHKeypointExamine psJHKeypointExamine = this.head.get();
        if (psJHKeypointExamine == null || list == null || list.size() == 0) {
            return false;
        }
        PsJHKeypointExamine keypointExamine = (PsJHKeypointExamine)list.get(0);
        String projectName = keypointExamine.getProjectName();



        List<PsJHKeypointExamine> psJHKeypointExamineList = new ArrayList<>();
        list.forEach(item ->{
            PsJHKeypointExamine jhKeypointExamine = (PsJHKeypointExamine)item;
            psJHKeypointExamineList.add(jhKeypointExamine);
        });

        return true;
    }

    @Override
    public void clear() {
        head.set(null);
    }
}
