/* YigoCAD工具生成,这个文件会被重新生成,请不要修改这个文件,也不要创建同名的文件. */
/* 生成时间 2026-01-04 09:54:18 */
package com.bokesoft.erp.billentity.pmconfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bokesoft.erp.entity.util.AbstractBillEntity;
import com.bokesoft.erp.entity.util.AbstractTableEntity;
import com.bokesoft.erp.entity.util.AbstractTableLoader;
import com.bokesoft.erp.entity.util.EntityArrayList;
import com.bokesoft.erp.entity.util.EntityContext;
import com.bokesoft.erp.entity.util.EntityUtil;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;
import com.bokesoft.yigo.common.def.SystemField;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.struct.datatable.DataTable;

/**
 * 维护策略主表(EPM_Strategy)数据库表<br>
 * 包含如下字段:对象标识(OID)<br>
 * 主对象标识(SOID)<br>
 * 父对象标识(POID)<br>
 * 对象版本(VERID)<br>
 * 对象明细版本(DVERID)<br>
 * 启用标记(Enable)<br>
 * TLeft(TLeft)<br>
 * TRight(TRight)<br>
 * 节点类型(NodeType)<br>
 * 上级节点(ParentID)<br>
 * 代码(Code)<br>
 * 名称(Name)<br>
 * 集团(ClientID)<br>
 * 创建人员(Creator)<br>
 * 创建时间(CreateTime)<br>
 * 修改人员(Modifier)<br>
 * 修改时间(ModifyTime)<br>
 * 备注(Notes)<br>
 * 计划标识(SchedulingIndicator)<br>
 * 策略单位(StrategyUnitID)<br>
 * 调用期(CallHorizon)<br>
 * 工厂日历(FactoryCalendarID)<br>
 * 延迟完成时的替换因子(LateShiftFactor)<br>
 * 提早完成时的替换因子(EarlyShiftFactor)<br>
 * 延迟完成的容差(LateTolerance)<br>
 * 提早完成的容差(EarlyTolerance)<br>
 * 事务码(TCodeID)<br>
 * 是否周期集(IsCycleSet)<br>
 * 单据Key(SystemVestKey)<br>
 * 
 * @author ERP研发部
 */
public class EPM_Strategy extends AbstractTableEntity {
    /**
     * 维护策略主表(EPM_Strategy)数据库表的表名
     */
    public static final String EPM_Strategy = "EPM_Strategy";
    private static class LazyHolder {
        private static final EPM_Strategy INSTANCE = new EPM_Strategy();
    }

    public static EPM_Strategy getInstance() {
        return LazyHolder.INSTANCE;
    }

    public PM_Strategy pM_Strategy;

    protected EPM_Strategy() {
        super();
        pM_Strategy = null;
    }
    
    /**
     * 根据数据创建维护策略主表(EPM_Strategy)数据库表实体
     * 
     * @param data
     * @param oid
     */
    public EPM_Strategy(RichDocumentContext context, AbstractBillEntity billEntity, DataTable data, Long oid, int rowIndex, String tableKey) {
        super(context, data, oid, rowIndex);
        if (billEntity instanceof PM_Strategy) {
            this.pM_Strategy = (PM_Strategy)billEntity;
        }
        this.billEntity = billEntity;
        this.tableKey = tableKey;
    }
    
    /**
     * 根据数据创建维护策略主表(EPM_Strategy)数据库表实体
     * 
     * @param data
     * @param oid
     */
    public EPM_Strategy(RichDocumentContext context, DataTable data, Long oid, int rowIndex) {
        super(context, data, oid, rowIndex);
        pM_Strategy = null;
        this.tableKey = EPM_Strategy;
    }
    
    /**
     * 根据数据创建维护策略主表(EPM_Strategy)数据库表实体
     * 
     * @param data
     * @param oid
     */
    public static EPM_Strategy parseRowset(RichDocumentContext context, DataTable data, Long oid, int rowIndex) {
        return new EPM_Strategy(context, data, oid, rowIndex);
    }
    
    /**
     * 解析DataTable创建维护策略主表(EPM_Strategy)数据库表实体列表
     * 
     * @param data
     * @throws Throwable
     */
    public static List<EPM_Strategy> parseRowset(RichDocumentContext context, DataTable data) throws Throwable {
        if (data == null) {
            return null;
        }
        int length = data.size();
        List<EPM_Strategy> result = new ArrayList<EPM_Strategy>(length);
        for (int rowIndex = 0; rowIndex < length; rowIndex++) {
            Long oid = data.getLong(rowIndex, SystemField.OID_SYS_KEY);
            EPM_Strategy resultDtl = parseRowset(context, data, oid, rowIndex);
            result.add(resultDtl);
        }
        return result;
    }
    
    @Override
    public AbstractBillEntity getBillEntity() {
        return pM_Strategy;
    }
    /**
     * 上级节点(ParentID)的数据库字段名<br>
     */
    public static final String ParentID = "ParentID";
    /**
     * 修改时间(ModifyTime)的数据库字段名<br>
     */
    public static final String ModifyTime = "ModifyTime";
    /**
     * 对象版本(VERID)的数据库字段名<br>
     */
    public static final String VERID = "VERID";
    /**
     * 是否周期集(IsCycleSet)的数据库字段名<br>
     */
    public static final String IsCycleSet = "IsCycleSet";
    /**
     * 创建人员(Creator)的数据库字段名<br>
     */
    public static final String Creator = "Creator";
    /**
     * 名称(Name)的数据库字段名<br>
     */
    public static final String Name = "Name";
    /**
     * 主对象标识(SOID)的数据库字段名<br>
     */
    public static final String SOID = "SOID";
    /**
     * 调用期(CallHorizon)的数据库字段名<br>
     */
    public static final String CallHorizon = "CallHorizon";
    /**
     * 工厂日历(FactoryCalendarID)的数据库字段名<br>
     */
    public static final String FactoryCalendarID = "FactoryCalendarID";
    /**
     * 事务码(TCodeID)的数据库字段名<br>
     */
    public static final String TCodeID = "TCodeID";
    /**
     * 启用标记(Enable)的数据库字段名<br>
     */
    public static final String Enable = "Enable";
    /**
     * 策略单位(StrategyUnitID)的数据库字段名<br>
     */
    public static final String StrategyUnitID = "StrategyUnitID";
    /**
     * 修改人员(Modifier)的数据库字段名<br>
     */
    public static final String Modifier = "Modifier";
    /**
     * 备注(Notes)的数据库字段名<br>
     */
    public static final String Notes = "Notes";
    /**
     * TRight的数据库字段名<br>
     */
    public static final String TRight = "TRight";
    /**
     * 创建时间(CreateTime)的数据库字段名<br>
     */
    public static final String CreateTime = "CreateTime";
    /**
     * 提早完成时的替换因子(EarlyShiftFactor)的数据库字段名<br>
     */
    public static final String EarlyShiftFactor = "EarlyShiftFactor";
    /**
     * 对象标识(OID)的数据库字段名<br>
     */
    public static final String OID = "OID";
    /**
     * 代码(Code)的数据库字段名<br>
     */
    public static final String Code = "Code";
    /**
     * 单据Key(SystemVestKey)的数据库字段名<br>
     */
    public static final String SystemVestKey = "SystemVestKey";
    /**
     * 延迟完成的容差(LateTolerance)的数据库字段名<br>
     */
    public static final String LateTolerance = "LateTolerance";
    /**
     * 提早完成的容差(EarlyTolerance)的数据库字段名<br>
     */
    public static final String EarlyTolerance = "EarlyTolerance";
    /**
     * 节点类型(NodeType)的数据库字段名<br>
     */
    public static final String NodeType = "NodeType";
    /**
     * TLeft的数据库字段名<br>
     */
    public static final String TLeft = "TLeft";
    /**
     * 延迟完成时的替换因子(LateShiftFactor)的数据库字段名<br>
     */
    public static final String LateShiftFactor = "LateShiftFactor";
    /**
     * 集团(ClientID)的数据库字段名<br>
     */
    public static final String ClientID = "ClientID";
    /**
     * 计划标识(SchedulingIndicator)的数据库字段名<br>
     */
    public static final String SchedulingIndicator = "SchedulingIndicator";
    /**
     * 对象明细版本(DVERID)的数据库字段名<br>
     */
    public static final String DVERID = "DVERID";
    /**
     * 父对象标识(POID)的数据库字段名<br>
     */
    public static final String POID = "POID";
    public static final Map<String, String> key2ColumnNames = new HashMap<String, String>();
    static {
        key2ColumnNames.put("OID", OID);
        key2ColumnNames.put("SOID", SOID);
        key2ColumnNames.put("POID", POID);
        key2ColumnNames.put("VERID", VERID);
        key2ColumnNames.put("DVERID", DVERID);
        key2ColumnNames.put("Enable", Enable);
        key2ColumnNames.put("TLeft", TLeft);
        key2ColumnNames.put("TRight", TRight);
        key2ColumnNames.put("NodeType", NodeType);
        key2ColumnNames.put("ParentID", ParentID);
        key2ColumnNames.put("Code", Code);
        key2ColumnNames.put("Name", Name);
        key2ColumnNames.put("ClientID", ClientID);
        key2ColumnNames.put("Creator", Creator);
        key2ColumnNames.put("CreateTime", CreateTime);
        key2ColumnNames.put("Modifier", Modifier);
        key2ColumnNames.put("ModifyTime", ModifyTime);
        key2ColumnNames.put("Notes", Notes);
        key2ColumnNames.put("SchedulingIndicator", SchedulingIndicator);
        key2ColumnNames.put("StrategyUnitID", StrategyUnitID);
        key2ColumnNames.put("CallHorizon", CallHorizon);
        key2ColumnNames.put("FactoryCalendarID", FactoryCalendarID);
        key2ColumnNames.put("LateShiftFactor", LateShiftFactor);
        key2ColumnNames.put("EarlyShiftFactor", EarlyShiftFactor);
        key2ColumnNames.put("LateTolerance", LateTolerance);
        key2ColumnNames.put("EarlyTolerance", EarlyTolerance);
        key2ColumnNames.put("TCodeID", TCodeID);
        key2ColumnNames.put("IsCycleSet", IsCycleSet);
        key2ColumnNames.put("SystemVestKey", SystemVestKey);
    }

    @Override
    protected String getColumnNameByKey(String columnName) {
        return key2ColumnNames.get(columnName);
    }
    
    public static String columnNameByKey(String columnName) {
        return key2ColumnNames.get(columnName);
    }

    
    private final static String langSQL = "select * from EPM_Strategy_T where SOID=? and srcLangOID=? order by oid";
    
    /**
     * 根据字段ColumnKey,取对象标识(OID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getOID() throws Throwable {
        return value_Long(OID);
    }
    
    /**
     * 根据字段ColumnKey,设置对象标识(OID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setOID(Long value) throws Throwable {
        valueByColumnName(OID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取主对象标识(SOID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getSOID() throws Throwable {
        return value_Long(SOID);
    }
    
    /**
     * 根据字段ColumnKey,设置主对象标识(SOID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setSOID(Long value) throws Throwable {
        valueByColumnName(SOID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取父对象标识(POID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getPOID() throws Throwable {
        return value_Long(POID);
    }
    
    /**
     * 根据字段ColumnKey,设置父对象标识(POID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setPOID(Long value) throws Throwable {
        valueByColumnName(POID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取对象版本(VERID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getVERID() throws Throwable {
        return value_Int(VERID);
    }
    
    /**
     * 根据字段ColumnKey,设置对象版本(VERID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setVERID(int value) throws Throwable {
        valueByColumnName(VERID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取对象明细版本(DVERID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getDVERID() throws Throwable {
        return value_Int(DVERID);
    }
    
    /**
     * 根据字段ColumnKey,设置对象明细版本(DVERID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setDVERID(int value) throws Throwable {
        valueByColumnName(DVERID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取启用标记(Enable)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getEnable() throws Throwable {
        return value_Int(Enable);
    }
    
    /**
     * 根据字段ColumnKey,设置启用标记(Enable)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setEnable(int value) throws Throwable {
        valueByColumnName(Enable, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取TLeft的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getTLeft() throws Throwable {
        return value_Int(TLeft);
    }
    
    /**
     * 根据字段ColumnKey,设置TLeft的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setTLeft(int value) throws Throwable {
        valueByColumnName(TLeft, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取TRight的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getTRight() throws Throwable {
        return value_Int(TRight);
    }
    
    /**
     * 根据字段ColumnKey,设置TRight的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setTRight(int value) throws Throwable {
        valueByColumnName(TRight, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取节点类型(NodeType)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getNodeType() throws Throwable {
        return value_Int(NodeType);
    }
    
    /**
     * 根据字段ColumnKey,设置节点类型(NodeType)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setNodeType(int value) throws Throwable {
        valueByColumnName(NodeType, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取上级节点(ParentID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getParentID() throws Throwable {
        return value_Long(ParentID);
    }
    
    /**
     * 根据字段ColumnKey,设置上级节点(ParentID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setParentID(Long value) throws Throwable {
        valueByColumnName(ParentID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取代码(Code)的值.
     * 
     * @return
     * @throws Throwable
     */
    public String getCode() throws Throwable {
        return value_String(Code);
    }
    
    /**
     * 根据字段ColumnKey,设置代码(Code)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setCode(String value) throws Throwable {
        valueByColumnName(Code, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取名称(Name)的值.
     * 
     * @return
     * @throws Throwable
     */
    public String getName() throws Throwable {
        Long oid = getOID();
        if (oid <= 0) {
            return null;
        }
        Long soid = getSOID();
        if (dataTable_T == null) {
            dataTable_T = context.getDBManager().execPrepareQuery(langSQL, soid, oid);
        }
        String str = null;
        String curLang = context.getEnv().getLocale();
        for (int i = 0; i < dataTable_T.size(); i++) {
            String t = dataTable_T.getString(i, Name);
            if (str == null) {
                str = t;
            } else if (curLang.equalsIgnoreCase(dataTable_T.getString(i, "lang")) && !t.isEmpty()) {
                str = t;
                break;
            }
        }
        return str == null ? StringUtil.EMPTY_STRING : str;
    }
    
    /**
     * 根据字段ColumnKey,取集团(ClientID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getClientID() throws Throwable {
        return value_Long(ClientID);
    }
    
    /**
     * 根据字段ColumnKey,设置集团(ClientID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setClientID(Long value) throws Throwable {
        valueByColumnName(ClientID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取创建人员(Creator)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getCreator() throws Throwable {
        return value_Long(Creator);
    }
    
    /**
     * 根据字段ColumnKey,取创建时间(CreateTime)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Timestamp getCreateTime() throws Throwable {
        return value_Timestamp(CreateTime);
    }
    
    /**
     * 根据字段ColumnKey,取修改人员(Modifier)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getModifier() throws Throwable {
        return value_Long(Modifier);
    }
    
    /**
     * 根据字段ColumnKey,取修改时间(ModifyTime)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Timestamp getModifyTime() throws Throwable {
        return value_Timestamp(ModifyTime);
    }
    
    /**
     * 根据字段ColumnKey,取备注(Notes)的值.
     * 
     * @return
     * @throws Throwable
     */
    public String getNotes() throws Throwable {
        return value_String(Notes);
    }
    
    /**
     * 根据字段ColumnKey,设置备注(Notes)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setNotes(String value) throws Throwable {
        valueByColumnName(Notes, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取计划标识(SchedulingIndicator)的值.
     * 
     * @return
     * @throws Throwable
     */
    public String getSchedulingIndicator() throws Throwable {
        return value_String(SchedulingIndicator);
    }
    
    /**
     * 根据字段ColumnKey,设置计划标识(SchedulingIndicator)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setSchedulingIndicator(String value) throws Throwable {
        valueByColumnName(SchedulingIndicator, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取策略单位(StrategyUnitID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getStrategyUnitID() throws Throwable {
        return value_Long(StrategyUnitID);
    }
    
    /**
     * 根据字段ColumnKey,设置策略单位(StrategyUnitID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setStrategyUnitID(Long value) throws Throwable {
        valueByColumnName(StrategyUnitID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取调用期(CallHorizon)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getCallHorizon() throws Throwable {
        return value_Int(CallHorizon);
    }
    
    /**
     * 根据字段ColumnKey,设置调用期(CallHorizon)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setCallHorizon(int value) throws Throwable {
        valueByColumnName(CallHorizon, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取工厂日历(FactoryCalendarID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getFactoryCalendarID() throws Throwable {
        return value_Long(FactoryCalendarID);
    }
    
    /**
     * 根据字段ColumnKey,设置工厂日历(FactoryCalendarID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setFactoryCalendarID(Long value) throws Throwable {
        valueByColumnName(FactoryCalendarID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取延迟完成时的替换因子(LateShiftFactor)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getLateShiftFactor() throws Throwable {
        return value_Int(LateShiftFactor);
    }
    
    /**
     * 根据字段ColumnKey,设置延迟完成时的替换因子(LateShiftFactor)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setLateShiftFactor(int value) throws Throwable {
        valueByColumnName(LateShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取提早完成时的替换因子(EarlyShiftFactor)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getEarlyShiftFactor() throws Throwable {
        return value_Int(EarlyShiftFactor);
    }
    
    /**
     * 根据字段ColumnKey,设置提早完成时的替换因子(EarlyShiftFactor)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setEarlyShiftFactor(int value) throws Throwable {
        valueByColumnName(EarlyShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取延迟完成的容差(LateTolerance)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getLateTolerance() throws Throwable {
        return value_Int(LateTolerance);
    }
    
    /**
     * 根据字段ColumnKey,设置延迟完成的容差(LateTolerance)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setLateTolerance(int value) throws Throwable {
        valueByColumnName(LateTolerance, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取提早完成的容差(EarlyTolerance)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getEarlyTolerance() throws Throwable {
        return value_Int(EarlyTolerance);
    }
    
    /**
     * 根据字段ColumnKey,设置提早完成的容差(EarlyTolerance)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setEarlyTolerance(int value) throws Throwable {
        valueByColumnName(EarlyTolerance, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取事务码(TCodeID)的值.
     * 
     * @return
     * @throws Throwable
     */
    public Long getTCodeID() throws Throwable {
        return value_Long(TCodeID);
    }
    
    /**
     * 根据字段ColumnKey,设置事务码(TCodeID)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setTCodeID(Long value) throws Throwable {
        valueByColumnName(TCodeID, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取是否周期集(IsCycleSet)的值.
     * 
     * @return
     * @throws Throwable
     */
    public int getIsCycleSet() throws Throwable {
        return value_Int(IsCycleSet);
    }
    
    /**
     * 根据字段ColumnKey,设置是否周期集(IsCycleSet)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setIsCycleSet(int value) throws Throwable {
        valueByColumnName(IsCycleSet, value);
        return this;
    }
    
    /**
     * 根据字段ColumnKey,取单据Key(SystemVestKey)的值.
     * 
     * @return
     * @throws Throwable
     */
    public String getSystemVestKey() throws Throwable {
        return value_String(SystemVestKey);
    }
    
    /**
     * 根据字段ColumnKey,设置单据Key(SystemVestKey)的值.
     * 
     * @param value
     * @throws Throwable
     */
    public EPM_Strategy setSystemVestKey(String value) throws Throwable {
        valueByColumnName(SystemVestKey, value);
        return this;
    }
    
    @Override
    public Long primaryID() throws Throwable {
        return getOID();
    }
    
    /**
     * 创建加载器
     * 
     * @param context 实体环境对象
     * @return
     * @throws Throwable
     */
    public static EPM_Strategy_Loader loader(RichDocumentContext context) throws Throwable {
        return new EPM_Strategy_Loader(context);
    }
    
    /**
     * 根据主键值取表对象
     * 
     * @param context 实体环境对象提供者
     * @param primaryValue 主键值
     * @return
     * @throws Throwable
     */
    public static EPM_Strategy load(RichDocumentContext context, Long primaryValue) throws Throwable {
        DataTable data = EntityContext.findTableEntityData(context, EPM_Strategy, primaryValue);
        if (data == null || data.size() == 0) {
            AbstractTableLoader.throwTableEntityNotNullError(EPM_Strategy.class, primaryValue);
        }
        return new EPM_Strategy(context, data, primaryValue, 0);
    }

    public static List<EPM_Strategy> getTableEntities(final RichDocumentContext context, final AbstractBillEntity parent, final Map<Long, EPM_Strategy> map) throws Throwable {
        List<EPM_Strategy> result = null;

        DataTable curRst = parent.document.get_impl(EPM_Strategy);
        if (curRst != null) {
            result = new EntityArrayList<>(context, parent, EPM_Strategy, EPM_Strategy.class, map);
        } else {
            result = EntityUtil.newSmallArrayList();
        }
        return result;
    }
    
    public static EPM_Strategy getTableEntitie(final RichDocumentContext context, final AbstractBillEntity parent, final Long oid) throws Throwable {
        EPM_Strategy result = null;

        DataTable curRst = parent.document.get_impl(EPM_Strategy);
        if (curRst != null && curRst.size() > 0) {
            int[] bks = curRst.fastFilter(SystemField.OID_SYS_KEY, oid);
            if (bks == null || bks.length == 0) {
                return result;
            }
            result = new EPM_Strategy(context, parent, curRst, oid, bks[0], EPM_Strategy);
        }
        return result;
    }
    

    public static EPM_Strategy of(AbstractBillEntity billEntity, String fieldKey) throws Throwable {
        Long oid = TypeConvertor.toLong(billEntity.getValue(fieldKey));
        if (oid.equals(0L)) {
            return com.bokesoft.erp.billentity.pmconfig.EPM_Strategy.getInstance();
        } else {
            return com.bokesoft.erp.billentity.pmconfig.EPM_Strategy.load(billEntity.getContext(), oid);
        }
    }

    public static EPM_Strategy of(AbstractTableEntity tableEntity, String columnName) throws Throwable {
        Long oid = TypeConvertor.toLong(tableEntity.valueByColumnName(columnName));
        if (oid.equals(0L)) {
            return com.bokesoft.erp.billentity.pmconfig.EPM_Strategy.getInstance();
        } else {
            return com.bokesoft.erp.billentity.pmconfig.EPM_Strategy.load(tableEntity.getContext(), oid);
        }
    }

}
