/* YigoCAD工具生成,这个文件会被重新生成,请不要修改这个文件,也不要创建同名的文件. */
/* 生成时间 2026-01-04 09:54:18 */
package com.bokesoft.erp.billentity.pmconfig;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bokesoft.erp.entity.util.AbstractBillEntity;
import com.bokesoft.erp.entity.util.AbstractTableEntity;
import com.bokesoft.erp.entity.util.DelayTableEntities;
import com.bokesoft.erp.entity.util.EntityArrayList;
import com.bokesoft.erp.entity.util.EntityUtil;
import com.bokesoft.yes.mid.cmd.richdocument.strut.IDLookup;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;
import com.bokesoft.yigo.common.def.SystemField;
import com.bokesoft.yigo.meta.form.MetaForm;
import com.bokesoft.yigo.struct.datatable.DataTable;
import com.bokesoft.yigo.struct.datatable.DataTableMetaData;

/**
 * 维护策略(PM_Strategy)<br>
 * 包含如下表实体:EPM_Strategy<br>
 * EPM_StrategyDtl<br>
 * 
 * @author ERP研发部
 */
public class PM_Strategy extends AbstractBillEntity {
    protected PM_Strategy() {}

    public static final String PM_Strategy = "PM_Strategy";
    /** 
     * 界面操作新增
     */
    public static final String Opt_DicNew = "DicNew";
     
    /** 
     * 界面操作复制新增
     */
    public static final String Opt_DicCopyNew = "DicCopyNew";
     
    /** 
     * 界面操作修改
     */
    public static final String Opt_DicModify = "DicModify";
     
    /** 
     * 界面操作保存
     */
    public static final String Opt_DicSave = "DicSave";
     
    /** 
     * 界面操作取消
     */
    public static final String Opt_DicCancel = "DicCancel";
     
    /** 
     * 界面操作启用
     */
    public static final String Opt_DicEnabled = "DicEnabled";
     
    /** 
     * 界面操作停用
     */
    public static final String Opt_DicDisabled = "DicDisabled";
     
    /** 
     * 界面操作作废
     */
    public static final String Opt_DicInvalid = "DicInvalid";
     
    /** 
     * 界面操作删除
     */
    public static final String Opt_DicDelete = "DicDelete";
     
    /** 
     * 界面操作刷新
     */
    public static final String Opt_DicRefresh = "DicRefresh";
     
    /** 
     * 界面操作多语言
     */
    public static final String Opt_Lang = "Lang";
     
    /** 
     * 界面操作查看数据日志
     */
    public static final String Opt_ShowDataLog = "ShowDataLog";
     
    /** 
     * 界面操作关闭
     */
    public static final String Opt_DicExit = "DicExit";
     
    /**
     * 单位(Dtl_PackageUnitID)的字段Key<br>
     */
    public static final String Dtl_PackageUnitID = "Dtl_PackageUnitID";
    /**
     * 修改时间(ModifyTime)的字段Key<br>
     */
    public static final String ModifyTime = "ModifyTime";
    /**
     * VERID(VERID)的字段Key<br>
     */
    public static final String VERID = "VERID";
    /**
     * 周期长度(Dtl_CycleLength)的字段Key<br>
     */
    public static final String Dtl_CycleLength = "Dtl_CycleLength";
    /**
     * 偏置(Dtl_OffsetPos)的字段Key<br>
     */
    public static final String Dtl_OffsetPos = "Dtl_OffsetPos";
    /**
     * 周期集(IsCycleSet)的字段Key<br>
     */
    public static final String IsCycleSet = "IsCycleSet";
    /**
     * 周期文本(Dtl_CycleNotes)的字段Key<br>
     */
    public static final String Dtl_CycleNotes = "Dtl_CycleNotes";
    /**
     * 制单人(Creator)的字段Key<br>
     */
    public static final String Creator = "Creator";
    /**
     * 制单日期(CreateDate)的字段Key<br>
     */
    public static final String CreateDate = "CreateDate";
    /**
     * 名称(Name)的字段Key<br>
     */
    public static final String Name = "Name";
    /**
     * %(Tag1)的字段Key<br>
     */
    public static final String Tag1 = "Tag1";
    /**
     * SOID(SOID)的字段Key<br>
     */
    public static final String SOID = "SOID";
    /**
     * 调用期(CallHorizon)的字段Key<br>
     */
    public static final String CallHorizon = "CallHorizon";
    /**
     * 事务码(TCodeID)的字段Key<br>
     */
    public static final String TCodeID = "TCodeID";
    /**
     * 后续(Dtl_FollowUpDays)的字段Key<br>
     */
    public static final String Dtl_FollowUpDays = "Dtl_FollowUpDays";
    /**
     * 工厂日历(FactoryCalendarID)的字段Key<br>
     */
    public static final String FactoryCalendarID = "FactoryCalendarID";
    /**
     * 状态(Enable)的字段Key<br>
     */
    public static final String Enable = "Enable";
    /**
     * 策略单位(StrategyUnitID)的字段Key<br>
     */
    public static final String StrategyUnitID = "StrategyUnitID";
    /**
     * 偏置短文本(Dtl_OffsetShortText)的字段Key<br>
     */
    public static final String Dtl_OffsetShortText = "Dtl_OffsetShortText";
    /**
     * %(Tag5)的字段Key<br>
     */
    public static final String Tag5 = "Tag5";
    /**
     * 修改人(Modifier)的字段Key<br>
     */
    public static final String Modifier = "Modifier";
    /**
     * %(Tag4)的字段Key<br>
     */
    public static final String Tag4 = "Tag4";
    /**
     * 初始(Dtl_LeadDays)的字段Key<br>
     */
    public static final String Dtl_LeadDays = "Dtl_LeadDays";
    /**
     * %(Tag3)的字段Key<br>
     */
    public static final String Tag3 = "Tag3";
    /**
     * Dtl_OID的字段Key<br>
     */
    public static final String Dtl_OID = "Dtl_OID";
    /**
     * %(Tag2)的字段Key<br>
     */
    public static final String Tag2 = "Tag2";
    /**
     * 维护包(Dtl_PackageNo)的字段Key<br>
     */
    public static final String Dtl_PackageNo = "Dtl_PackageNo";
    /**
     * 层次(Dtl_CycleHierarchy)的字段Key<br>
     */
    public static final String Dtl_CycleHierarchy = "Dtl_CycleHierarchy";
    /**
     * 制单时间(CreateTime)的字段Key<br>
     */
    public static final String CreateTime = "CreateTime";
    /**
     * 周期短文本(Dtl_CycleShortText)的字段Key<br>
     */
    public static final String Dtl_CycleShortText = "Dtl_CycleShortText";
    /**
     * 提早完成时的替换因子(EarlyShiftFactor)的字段Key<br>
     */
    public static final String EarlyShiftFactor = "EarlyShiftFactor";
    /**
     * 代码(Code)的字段Key<br>
     */
    public static final String Code = "Code";
    /**
     * 包顺序(PackSequence)的字段Key<br>
     */
    public static final String PackSequence = "PackSequence";
    /**
     * 选择(Dtl_IsSelect)的字段Key<br>
     */
    public static final String Dtl_IsSelect = "Dtl_IsSelect";
    /**
     * 延迟完成的容差(LateTolerance)的字段Key<br>
     */
    public static final String LateTolerance = "LateTolerance";
    /**
     * 提早完成的容差(EarlyTolerance)的字段Key<br>
     */
    public static final String EarlyTolerance = "EarlyTolerance";
    /**
     * 节点类型(NodeType)的字段Key<br>
     */
    public static final String NodeType = "NodeType";
    /**
     * 延迟完成时的替换因子(LateShiftFactor)的字段Key<br>
     */
    public static final String LateShiftFactor = "LateShiftFactor";
    /**
     * 系统环境(ClientID)的字段Key<br>
     */
    public static final String ClientID = "ClientID";
    /**
     * 隐藏信息(HiddenInformation)的字段Key<br>
     */
    public static final String HiddenInformation = "HiddenInformation";
    /**
     * 计划标识(SchedulingIndicator)的字段Key<br>
     */
    public static final String SchedulingIndicator = "SchedulingIndicator";
    /**
     * DVERID(DVERID)的字段Key<br>
     */
    public static final String DVERID = "DVERID";
    /**
     * 基本数值(Dtl_BaseLength)的字段Key<br>
     */
    public static final String Dtl_BaseLength = "Dtl_BaseLength";
    /**
     * POID(POID)的字段Key<br>
     */
    public static final String POID = "POID";
    private EPM_Strategy epm_strategy;
    private List<EPM_StrategyDtl> epm_strategyDtls;
    private List<EPM_StrategyDtl> epm_strategyDtl_tmp;
    private Map<Long, EPM_StrategyDtl> epm_strategyDtlMap;
    private boolean epm_strategyDtl_init;
    
    /**
     * 延迟创建表实体对象
     * 
     * @throws Throwable
     */
    private void initEPM_Strategy() throws Throwable {
        if (epm_strategy != null) {
            return;
        }
        DataTable rst = document.get_impl(EPM_Strategy.EPM_Strategy);
        if (rst.first()) {
            epm_strategy = new EPM_Strategy(this.document.getContext(), this, rst, rst.getLong(SystemField.OID_SYS_KEY), 0, EPM_Strategy.EPM_Strategy);
        }
    }
    
    public void initEPM_StrategyDtls() throws Throwable {
        if (epm_strategyDtl_init) {
            return;
        }
        epm_strategyDtlMap = new HashMap<>();
        epm_strategyDtls = EPM_StrategyDtl.getTableEntities(this.document.getContext(), this, epm_strategyDtlMap);
        epm_strategyDtl_init = true;
    }
    
    /**
     * 根据数据对象生成维护策略(PM_Strategy)的实体对象<br>
     * 
     * @param _context
     * @return
     * @throws Throwable
     */
    public static PM_Strategy parseEntity(RichDocumentContext _context) throws Throwable {
        PM_Strategy result = parseDocument(_context.getRichDocument());
        return result;
    }
    
    /**
     * 根据数据对象生成维护策略(PM_Strategy)的实体对象<br>
     * 如果数据对象的表单不是维护策略(PM_Strategy),则抛出RuntimeException.
     * 
     * @param doc
     * @return
     * @throws Throwable
     */
    public static PM_Strategy parseDocument(RichDocument doc) throws Throwable {
        MetaForm metaForm = doc.getMetaForm();
        if (!IDLookup.getSourceKey(metaForm).equals(PM_Strategy)) {
            throw new RuntimeException("数据对象不是维护策略(PM_Strategy)的数据对象,无法生成维护策略(PM_Strategy)实体.");
        }
        PM_Strategy result = new PM_Strategy();
        result.document = doc;
        return result;
    }
    
    /** 为了解析BKRowSet返回表单数组所用的ID */
    private Long idForParseRowSet;
    
    /**
     * 解析BKRowSet返回表单数组
     * 
     * @param rst
     * @return
     * @throws Throwable
     */
    public static List<PM_Strategy> parseRowSet(RichDocumentContext context, DataTable rst) throws Throwable {
        if (rst == null) {
            return null;
        }
        int length = rst.size();
        List<PM_Strategy> result = new ArrayList<PM_Strategy>(length);
        for (int rowIndex = 0; rowIndex < length; rowIndex++)  {
            Long oid = rst.getLong(rowIndex, SystemField.OID_SYS_KEY);
            PM_Strategy entity = null;
            for (PM_Strategy tmp : result) {
                if (tmp.idForParseRowSet.equals(oid)) {
                    entity = tmp;
                    break;
                }
            }
            if (entity == null) {
                entity = new PM_Strategy();
                entity.idForParseRowSet = oid;
                result.add(entity);
            }
    
            DataTableMetaData metaData = rst.getMetaData();
            if (metaData.constains("EPM_Strategy_ID")) {
                entity.epm_strategy = new EPM_Strategy(context, rst, oid, rowIndex);
            }
            if (metaData.constains("EPM_StrategyDtl_ID")) {
                if (entity.epm_strategyDtls == null) {
                    entity.epm_strategyDtls = new DelayTableEntities<>();
                    entity.epm_strategyDtlMap = new HashMap<>();
                }
                EPM_StrategyDtl dtl = new EPM_StrategyDtl(context, rst, oid, rowIndex);
                entity.epm_strategyDtls.add(dtl);
                entity.epm_strategyDtlMap.put(oid, dtl);
            }
        }
        return result;
    }
    
    /**
     * 延后删除,解决ConcurrentModificationException报错
     */
    private void deleteALLTmp() {
        if (epm_strategyDtls != null && epm_strategyDtl_tmp != null && epm_strategyDtl_tmp.size() > 0) {
            epm_strategyDtls.removeAll(epm_strategyDtl_tmp);
            epm_strategyDtl_tmp.clear();
            epm_strategyDtl_tmp = null;
        }
    }
    
    private static MetaForm metaForm;
    
    public static MetaForm metaForm(RichDocumentContext midContext) throws Throwable {
        if (metaForm == null) {
            metaForm = midContext.getMetaFactory().getMetaForm("PM_Strategy");
        }
        return metaForm;
    }
    
    /**
     * 取EPM_Strategy实体
     * 
     * @return
     * @throws Throwable
     */
    public EPM_Strategy epm_strategy() throws Throwable {
        initEPM_Strategy();
        return epm_strategy;
    }
    
    /**
     * 取EPM_StrategyDtl实体列表
     * 
     * @return
     * @throws Throwable
     */
    public List<EPM_StrategyDtl> epm_strategyDtls() throws Throwable {
        deleteALLTmp();
        initEPM_StrategyDtls();
        return new ArrayList<>(epm_strategyDtls);
    }
    
    public int epm_strategyDtlSize() throws Throwable {
        deleteALLTmp();
        initEPM_StrategyDtls();
        return epm_strategyDtls.size();
    }
    
    /**
     * 根据主键值,取EPM_StrategyDtl实体对象
     * 
     * @param primaryValue
     * @return
     * @throws Throwable
     */
    public EPM_StrategyDtl epm_strategyDtl(Long primaryValue) throws Throwable {
        deleteALLTmp();
        if (primaryValue <= 0) {
            return null;
        }

        if (epm_strategyDtl_init) {
            if (epm_strategyDtlMap.containsKey(primaryValue)) {
                return epm_strategyDtlMap.get(primaryValue);
            } else {
                EPM_StrategyDtl dtl = EPM_StrategyDtl.getTableEntitie(this.document.getContext(), this, primaryValue);
                epm_strategyDtlMap.put(primaryValue, dtl);
                return dtl;
            }
        }
        if (epm_strategyDtls == null) {
            epm_strategyDtls = new ArrayList<>();
            epm_strategyDtlMap = new HashMap<>();
        } else {
            if (epm_strategyDtlMap.containsKey(primaryValue)) {
                return epm_strategyDtlMap.get(primaryValue);
            }
        }
        
        EPM_StrategyDtl dtl = EPM_StrategyDtl.getTableEntitie(this.document.getContext(), this, primaryValue);
        if (dtl == null) {
            return null;
        }

        epm_strategyDtls.add(dtl);
        epm_strategyDtlMap.put(primaryValue, dtl);
        return dtl;
    }
    
    /**
     * 根据字段key和值,取EPM_StrategyDtl实体列表
     * 
     * @param filterKey
     * @param filterValue
     * @return
     * @throws Throwable
     */
    public List<EPM_StrategyDtl> epm_strategyDtls(String filterKey, Object filterValue) throws Throwable {
        String columnName = EPM_StrategyDtl.key2ColumnNames.get(filterKey);
        return EntityUtil.filter(epm_strategyDtls(), columnName, filterValue);
    }
    
    /**
     * 新增EPM_StrategyDtl实体对象
     * 
     * @return
     */
    public EPM_StrategyDtl newEPM_StrategyDtl() throws Throwable {
        deleteALLTmp();
        int rowIndex = document.appendDetail(EPM_StrategyDtl.EPM_StrategyDtl);
        DataTable rst = document.get_impl(EPM_StrategyDtl.EPM_StrategyDtl);
        Long oid = rst.getLong(rowIndex, SystemField.OID_SYS_KEY);
        EPM_StrategyDtl dtl = new EPM_StrategyDtl(this.document.getContext(), this, rst, oid, rowIndex, EPM_StrategyDtl.EPM_StrategyDtl);
        if (!epm_strategyDtl_init) {
            epm_strategyDtls = new ArrayList<>();
            epm_strategyDtlMap = new HashMap<>();
        }
        epm_strategyDtls.add(dtl);
        epm_strategyDtlMap.put(oid, dtl);
        return dtl;
    }
    
    /**
     * 删除EPM_StrategyDtl实体对象
     */
    public void deleteEPM_StrategyDtl(EPM_StrategyDtl dtl) throws Throwable {
        if (epm_strategyDtl_tmp == null) {
            epm_strategyDtl_tmp = new ArrayList<>();
        }
        epm_strategyDtl_tmp.add(dtl);

        if (epm_strategyDtls instanceof EntityArrayList) {
            ((EntityArrayList)epm_strategyDtls).initAll();
        }
        if (epm_strategyDtlMap != null) {
            epm_strategyDtlMap.remove(dtl.oid);
        }
        document.deleteDetail(EPM_StrategyDtl.EPM_StrategyDtl, dtl.oid);
    }
    
    /**
     * 根据字段Key,取字段修改时间(ModifyTime)的值
     * 
     * @return
     * @throws Throwable
     */
    public Timestamp getModifyTime() throws Throwable {
        return value_Timestamp(ModifyTime);
    }
    
    /**
     * 根据字段Key,取字段周期集(IsCycleSet)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getIsCycleSet() throws Throwable {
        return value_Int(IsCycleSet);
    }
    
    /**
     * 根据字段Key,设置字段周期集(IsCycleSet)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setIsCycleSet(int value) throws Throwable {
        setValue(IsCycleSet, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段制单人(Creator)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getCreator() throws Throwable {
        return value_Long(Creator);
    }
    
    /**
     * 根据字段Key,取字段制单日期(CreateDate)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getCreateDate() throws Throwable {
        return value_Long(CreateDate);
    }
    
    /**
     * 根据字段Key,设置字段制单日期(CreateDate)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setCreateDate(Long value) throws Throwable {
        setValue(CreateDate, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段名称(Name)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getName() throws Throwable {
        return value_String(Name);
    }
    
    /**
     * 根据字段Key,设置字段名称(Name)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setName(String value) throws Throwable {
        setValue(Name, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段%(Tag1)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getTag1() throws Throwable {
        return value_String(Tag1);
    }
    
    /**
     * 根据字段Key,设置字段%(Tag1)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTag1(String value) throws Throwable {
        setValue(Tag1, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段SOID(SOID)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getSOID() throws Throwable {
        return value_Long(SOID);
    }
    
    /**
     * 根据字段Key,设置字段SOID(SOID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setSOID(Long value) throws Throwable {
        setValue(SOID, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段调用期(CallHorizon)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getCallHorizon() throws Throwable {
        return value_Int(CallHorizon);
    }
    
    /**
     * 根据字段Key,设置字段调用期(CallHorizon)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setCallHorizon(int value) throws Throwable {
        setValue(CallHorizon, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段事务码(TCodeID)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getTCodeID() throws Throwable {
        return value_Long(TCodeID);
    }
    
    /**
     * 根据字段Key,设置字段事务码(TCodeID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTCodeID(Long value) throws Throwable {
        setValue(TCodeID, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段工厂日历(FactoryCalendarID)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getFactoryCalendarID() throws Throwable {
        return value_Long(FactoryCalendarID);
    }
    
    /**
     * 根据字段Key,设置字段工厂日历(FactoryCalendarID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setFactoryCalendarID(Long value) throws Throwable {
        setValue(FactoryCalendarID, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段状态(Enable)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getEnable() throws Throwable {
        return value_Int(Enable);
    }
    
    /**
     * 根据字段Key,设置字段状态(Enable)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setEnable(int value) throws Throwable {
        setValue(Enable, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段策略单位(StrategyUnitID)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getStrategyUnitID() throws Throwable {
        return value_Long(StrategyUnitID);
    }
    
    /**
     * 根据字段Key,设置字段策略单位(StrategyUnitID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setStrategyUnitID(Long value) throws Throwable {
        setValue(StrategyUnitID, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段%(Tag5)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getTag5() throws Throwable {
        return value_String(Tag5);
    }
    
    /**
     * 根据字段Key,设置字段%(Tag5)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTag5(String value) throws Throwable {
        setValue(Tag5, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段修改人(Modifier)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getModifier() throws Throwable {
        return value_Long(Modifier);
    }
    
    /**
     * 根据字段Key,取字段%(Tag4)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getTag4() throws Throwable {
        return value_String(Tag4);
    }
    
    /**
     * 根据字段Key,设置字段%(Tag4)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTag4(String value) throws Throwable {
        setValue(Tag4, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段%(Tag3)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getTag3() throws Throwable {
        return value_String(Tag3);
    }
    
    /**
     * 根据字段Key,设置字段%(Tag3)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTag3(String value) throws Throwable {
        setValue(Tag3, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段%(Tag2)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getTag2() throws Throwable {
        return value_String(Tag2);
    }
    
    /**
     * 根据字段Key,设置字段%(Tag2)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setTag2(String value) throws Throwable {
        setValue(Tag2, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段制单时间(CreateTime)的值
     * 
     * @return
     * @throws Throwable
     */
    public Timestamp getCreateTime() throws Throwable {
        return value_Timestamp(CreateTime);
    }
    
    /**
     * 根据字段Key,取字段提早完成时的替换因子(EarlyShiftFactor)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getEarlyShiftFactor() throws Throwable {
        return value_Int(EarlyShiftFactor);
    }
    
    /**
     * 根据字段Key,设置字段提早完成时的替换因子(EarlyShiftFactor)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setEarlyShiftFactor(int value) throws Throwable {
        setValue(EarlyShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段代码(Code)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getCode() throws Throwable {
        return value_String(Code);
    }
    
    /**
     * 根据字段Key,设置字段代码(Code)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setCode(String value) throws Throwable {
        setValue(Code, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段延迟完成的容差(LateTolerance)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getLateTolerance() throws Throwable {
        return value_Int(LateTolerance);
    }
    
    /**
     * 根据字段Key,设置字段延迟完成的容差(LateTolerance)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setLateTolerance(int value) throws Throwable {
        setValue(LateTolerance, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段提早完成的容差(EarlyTolerance)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getEarlyTolerance() throws Throwable {
        return value_Int(EarlyTolerance);
    }
    
    /**
     * 根据字段Key,设置字段提早完成的容差(EarlyTolerance)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setEarlyTolerance(int value) throws Throwable {
        setValue(EarlyTolerance, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段节点类型(NodeType)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getNodeType() throws Throwable {
        return value_Int(NodeType);
    }
    
    /**
     * 根据字段Key,设置字段节点类型(NodeType)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setNodeType(int value) throws Throwable {
        setValue(NodeType, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段延迟完成时的替换因子(LateShiftFactor)的值
     * 
     * @return
     * @throws Throwable
     */
    public int getLateShiftFactor() throws Throwable {
        return value_Int(LateShiftFactor);
    }
    
    /**
     * 根据字段Key,设置字段延迟完成时的替换因子(LateShiftFactor)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setLateShiftFactor(int value) throws Throwable {
        setValue(LateShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段系统环境(ClientID)的值
     * 
     * @return
     * @throws Throwable
     */
    public Long getClientID() throws Throwable {
        return value_Long(ClientID);
    }
    
    /**
     * 根据字段Key,设置字段系统环境(ClientID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setClientID(Long value) throws Throwable {
        setValue(ClientID, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段隐藏信息(HiddenInformation)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getHiddenInformation() throws Throwable {
        return value_String(HiddenInformation);
    }
    
    /**
     * 根据字段Key,设置字段隐藏信息(HiddenInformation)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setHiddenInformation(String value) throws Throwable {
        setValue(HiddenInformation, value);
        return this;
    }
    
    /**
     * 根据字段Key,取字段计划标识(SchedulingIndicator)的值
     * 
     * @return
     * @throws Throwable
     */
    public String getSchedulingIndicator() throws Throwable {
        return value_String(SchedulingIndicator);
    }
    
    /**
     * 根据字段Key,设置字段计划标识(SchedulingIndicator)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setSchedulingIndicator(String value) throws Throwable {
        setValue(SchedulingIndicator, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段维护包(Dtl_PackageNo)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_PackageNo(Long oid) throws Throwable {
        return value_Int(Dtl_PackageNo, oid);
    }
    
    /**
     * 根据字段Key,设置字段维护包(Dtl_PackageNo)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_PackageNo(Long oid, int value) throws Throwable {
        setValue(Dtl_PackageNo, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段单位(Dtl_PackageUnitID)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public Long getDtl_PackageUnitID(Long oid) throws Throwable {
        return value_Long(Dtl_PackageUnitID, oid);
    }
    
    /**
     * 根据字段Key,设置字段单位(Dtl_PackageUnitID)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_PackageUnitID(Long oid, Long value) throws Throwable {
        setValue(Dtl_PackageUnitID, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段周期长度(Dtl_CycleLength)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_CycleLength(Long oid) throws Throwable {
        return value_Int(Dtl_CycleLength, oid);
    }
    
    /**
     * 根据字段Key,设置字段周期长度(Dtl_CycleLength)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_CycleLength(Long oid, int value) throws Throwable {
        setValue(Dtl_CycleLength, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段偏置(Dtl_OffsetPos)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_OffsetPos(Long oid) throws Throwable {
        return value_Int(Dtl_OffsetPos, oid);
    }
    
    /**
     * 根据字段Key,设置字段偏置(Dtl_OffsetPos)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_OffsetPos(Long oid, int value) throws Throwable {
        setValue(Dtl_OffsetPos, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段周期短文本(Dtl_CycleShortText)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public String getDtl_CycleShortText(Long oid) throws Throwable {
        return value_String(Dtl_CycleShortText, oid);
    }
    
    /**
     * 根据字段Key,设置字段周期短文本(Dtl_CycleShortText)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_CycleShortText(Long oid, String value) throws Throwable {
        setValue(Dtl_CycleShortText, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段层次(Dtl_CycleHierarchy)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_CycleHierarchy(Long oid) throws Throwable {
        return value_Int(Dtl_CycleHierarchy, oid);
    }
    
    /**
     * 根据字段Key,设置字段层次(Dtl_CycleHierarchy)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_CycleHierarchy(Long oid, int value) throws Throwable {
        setValue(Dtl_CycleHierarchy, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段周期文本(Dtl_CycleNotes)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public String getDtl_CycleNotes(Long oid) throws Throwable {
        return value_String(Dtl_CycleNotes, oid);
    }
    
    /**
     * 根据字段Key,设置字段周期文本(Dtl_CycleNotes)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_CycleNotes(Long oid, String value) throws Throwable {
        setValue(Dtl_CycleNotes, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段选择(Dtl_IsSelect)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_IsSelect(Long oid) throws Throwable {
        return value_Int(Dtl_IsSelect, oid);
    }
    
    /**
     * 根据字段Key,设置字段选择(Dtl_IsSelect)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_IsSelect(Long oid, int value) throws Throwable {
        setValue(Dtl_IsSelect, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段后续(Dtl_FollowUpDays)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_FollowUpDays(Long oid) throws Throwable {
        return value_Int(Dtl_FollowUpDays, oid);
    }
    
    /**
     * 根据字段Key,设置字段后续(Dtl_FollowUpDays)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_FollowUpDays(Long oid, int value) throws Throwable {
        setValue(Dtl_FollowUpDays, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段偏置短文本(Dtl_OffsetShortText)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public String getDtl_OffsetShortText(Long oid) throws Throwable {
        return value_String(Dtl_OffsetShortText, oid);
    }
    
    /**
     * 根据字段Key,设置字段偏置短文本(Dtl_OffsetShortText)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_OffsetShortText(Long oid, String value) throws Throwable {
        setValue(Dtl_OffsetShortText, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段初始(Dtl_LeadDays)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public int getDtl_LeadDays(Long oid) throws Throwable {
        return value_Int(Dtl_LeadDays, oid);
    }
    
    /**
     * 根据字段Key,设置字段初始(Dtl_LeadDays)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_LeadDays(Long oid, int value) throws Throwable {
        setValue(Dtl_LeadDays, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段基本数值(Dtl_BaseLength)的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public BigDecimal getDtl_BaseLength(Long oid) throws Throwable {
        return value_BigDecimal(Dtl_BaseLength, oid);
    }
    
    /**
     * 根据字段Key,设置字段基本数值(Dtl_BaseLength)的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_BaseLength(Long oid, BigDecimal value) throws Throwable {
        setValue(Dtl_BaseLength, oid, value);
        return this;
    }
    
    /**
     * 根据字段Key和oid,取字段Dtl_OID的值
     * 
     * @param oid
     * @return 
     * @throws Throwable
     */
    public Long getDtl_OID(Long oid) throws Throwable {
        return value_Long(Dtl_OID, oid);
    }
    
    /**
     * 根据字段Key,设置字段Dtl_OID的值
     * 
     * @param value
     * @throws Throwable
     */
    public PM_Strategy setDtl_OID(Long oid, Long value) throws Throwable {
        setValue(Dtl_OID, oid, value);
        return this;
    }
    
    /**
    * 得到字典的显示值(没有根据配置属性来处理)
    * @return
    * @throws Throwable
    */
    public String getCodeName() throws Throwable {
        initEPM_Strategy();
        return epm_strategy.getCode() + " " + epm_strategy.getName();
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractTableEntity> List<T> tableEntities(Class<T> tableEntityClass) throws Throwable {
        if (tableEntityClass == EPM_Strategy.class) {
            initEPM_Strategy();
            List<T> result = new ArrayList<T>(1);
            result.add((T)epm_strategy);
            return result;
        }
        if (tableEntityClass == EPM_StrategyDtl.class) {
            initEPM_StrategyDtls();
            return (List<T>)epm_strategyDtls;
        }
        throw new RuntimeException();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractTableEntity> T newTableEntity(Class<T> tableEntityClass) throws Throwable {
        if (tableEntityClass == EPM_Strategy.class) {
            throw new RuntimeException("头表不能新增");
        }
        if (tableEntityClass == EPM_StrategyDtl.class) {
            return (T)this.newEPM_StrategyDtl();
        }
        throw new RuntimeException("不存在的表类型");
    }
    
    @Override
    public void deleteTableEntity(AbstractTableEntity tableEntity) throws Throwable {
        if (tableEntity instanceof EPM_Strategy) {
            throw new RuntimeException("头表不能删除");
        }
        if (tableEntity instanceof EPM_StrategyDtl) {
            this.deleteEPM_StrategyDtl((EPM_StrategyDtl) tableEntity);
            return;
        }
        throw new RuntimeException("不存在的表类型");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractTableEntity> Map<String, Class<T>> allTableEntitieClasss() {
        Map<String, Class<T>> result = new LinkedHashMap(2);
        result.put(EPM_Strategy.EPM_Strategy, (Class<T>)EPM_Strategy.class);
        result.put(EPM_StrategyDtl.EPM_StrategyDtl, (Class<T>)EPM_StrategyDtl.class);
        return result;
    }
    
    @Override
    public String toString() {
        /*try {
            initAll();
        } catch (Throwable e) {
        }*/
        return "PM_Strategy:" + (epm_strategy == null ? "Null" : epm_strategy.toString()) + ", "
            + (epm_strategyDtls == null ? "Null" : epm_strategyDtls.toString());
    }
    
    /** 
     * 创建加载器
     * 
     * @param context 实体环境对象提供者
     * @return 
     * @throws Throwable
     */
    public static PM_Strategy_Loader loader(RichDocumentContext context) throws Throwable {
        return new PM_Strategy_Loader(context);
    }
    
    /** 
     * 根据主键值取对象
     * 
     * @param context 实体环境对象提供者
     * @param primaryValue 主键值
     * @return 
     * @throws Throwable
     */
    public static PM_Strategy load(RichDocumentContext context, Long primaryValue) throws Throwable {
        return new PM_Strategy_Loader(context).load(primaryValue);
    }
    
    /**
     * 状态的停用
     */
    public static final int Enable_0 = 0;
    
    /**
     * 状态的启用
     */
    public static final int Enable_1 = 1;
    
    /**
     * 状态的作废
     */
    public static final int Enable_Neg1 = -1;
    
    /**
     * 节点类型的明细节点
     */
    public static final int NodeType_0 = 0;
    
    /**
     * 节点类型的汇总节点
     */
    public static final int NodeType_1 = 1;
    
    /**
     * 计划标识的时间
     */
    public static final int SchedulingIndicator_0 = 0;
    
    /**
     * 计划标识的时间-关键日期
     */
    public static final int SchedulingIndicator_1 = 1;
    
    /**
     * 计划标识的时间-工厂日历
     */
    public static final int SchedulingIndicator_2 = 2;
    
    /**
     * 计划标识的基于性能的
     */
    public static final int SchedulingIndicator_3 = 3;
    
    /**
     * 计划标识的多计数器
     */
    public static final int SchedulingIndicator_4 = 4;
    
}
