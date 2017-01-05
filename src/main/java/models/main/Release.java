package models.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import manament.log.LoggerWapper;
public enum Release {
    
    R1_3_0("1.3.0"), R1_2_0("1.2.0"), R1_2_01("1.2.01");

    final static LoggerWapper logger = LoggerWapper.getLogger(Release.class);
    private String value;
    
    private Release(String str) {
        value = str;
    }

    @JsonCreator
    public static Release fromString(String str) {
        if(str != null){
            if("1.3.0".equals(str) || "130".equals(str)){
                return R1_3_0;
            } else if("1.2.0".equals(str) || "120".equals(str)){
                return R1_2_0;
            } else if("1.2.01".equals(str) || "1201".equals(str)){
                return R1_2_01;
            }
        }else{
            logger.fastDebug("Release not found:%s",str);
        }
        return null;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

}
