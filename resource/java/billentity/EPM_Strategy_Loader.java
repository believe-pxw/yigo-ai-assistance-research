/* YigoCAD工具生成,这个文件会被重新生成,请不要修改这个文件,也不要创建同名的文件. */
/* 生成时间 2026-01-04 09:54:18 */
package com.bokesoft.erp.billentity.pmconfig;

import java.util.ArrayList;
import java.util.List;

import com.bokesoft.erp.entity.util.AbstractTableLoader;
import com.bokesoft.erp.entity.util.EntityContext;
import com.bokesoft.erp.entity.util.OrderByExpression;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;
import com.bokesoft.yigo.common.def.SystemField;
import com.bokesoft.yigo.struct.datatable.DataTable;

/**
 * 维护策略主表(EPM_Strategy)数据库表;<br>
 * 包含如下字段:对象标识(OID)<br>
 * 主对象标识(SOID)<br>
 * 父对象标识(POID)<br>
 * 对象版本(VERID)<br>
 * 对象明细版本(DVERID)<br>
 * 启用标记(Enable)<br>
 * (TLeft)<br>
 * (TRight)<br>
 * 节点类型(NodeType)<br>
 * 上级节点(ParentID)<br>
 * 代码(Code)<br>
 * 名称(Name)<br>
 * 集团(ClientID)<br>
 * 创建人员(Creator)<br>
 * 创建时间(CreateTime)<br>
 * 制单日期(CreateDate)<br>
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
 * (MapCount)<br>
 * (LastModified)<br>
 * 
 * @author ERP研发部
 */
public class EPM_Strategy_Loader extends AbstractTableLoader<EPM_Strategy_Loader> {

    protected EPM_Strategy_Loader(RichDocumentContext context) throws Throwable {
        super(context, EPM_Strategy.EPM_Strategy);
    }
    
    @Override
    public boolean existCluster() {
        return true;
    }
    
    @Override
    public String clusterKey() {
        return EPM_Strategy.ClientID;
    }
    
    /**
     * 根据字段对象标识(OID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader OID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.OID, value);
        return this;
    }
    
    public EPM_Strategy_Loader OID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.OID, value);
        return this;
    }
    
    public EPM_Strategy_Loader OID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.OID, operator, value);
        return this;
    }
    
    /**
     * 根据字段主对象标识(SOID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader SOID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.OID, value);
        return this;
    }
    
    public EPM_Strategy_Loader SOID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.OID, value);
        return this;
    }
    
    public EPM_Strategy_Loader SOID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.OID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader SOIDPreLoad(Long value, String preLoadTableName) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.OID, value, preLoadTableName);
        return this;
    }
    
    /**
     * 根据字段父对象标识(POID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader POID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.POID, value);
        return this;
    }
    
    public EPM_Strategy_Loader POID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.POID, value);
        return this;
    }
    
    public EPM_Strategy_Loader POID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.POID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader POIDPreLoad(Long value, String preLoadTableName) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.POID, value, preLoadTableName);
        return this;
    }
    
    /**
     * 根据字段对象版本(VERID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader VERID(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.VERID, value);
        return this;
    }
    
    public EPM_Strategy_Loader VERID(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.VERID, value);
        return this;
    }
    
    public EPM_Strategy_Loader VERID(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.VERID, operator, value);
        return this;
    }
    
    /**
     * 根据字段对象明细版本(DVERID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader DVERID(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.DVERID, value);
        return this;
    }
    
    public EPM_Strategy_Loader DVERID(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.DVERID, value);
        return this;
    }
    
    public EPM_Strategy_Loader DVERID(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.DVERID, operator, value);
        return this;
    }
    
    /**
     * 根据字段启用标记(Enable)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader Enable(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Enable, value);
        return this;
    }
    
    public EPM_Strategy_Loader Enable(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Enable, value);
        return this;
    }
    
    public EPM_Strategy_Loader Enable(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.Enable, operator, value);
        return this;
    }
    
    /**
     * 根据字段(TLeft)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader TLeft(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TLeft, value);
        return this;
    }
    
    public EPM_Strategy_Loader TLeft(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TLeft, value);
        return this;
    }
    
    public EPM_Strategy_Loader TLeft(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.TLeft, operator, value);
        return this;
    }
    
    /**
     * 根据字段(TRight)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader TRight(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TRight, value);
        return this;
    }
    
    public EPM_Strategy_Loader TRight(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TRight, value);
        return this;
    }
    
    public EPM_Strategy_Loader TRight(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.TRight, operator, value);
        return this;
    }
    
    /**
     * 根据字段节点类型(NodeType)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader NodeType(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.NodeType, value);
        return this;
    }
    
    public EPM_Strategy_Loader NodeType(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.NodeType, value);
        return this;
    }
    
    public EPM_Strategy_Loader NodeType(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.NodeType, operator, value);
        return this;
    }
    
    /**
     * 根据字段上级节点(ParentID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader ParentID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.ParentID, value);
        return this;
    }
    
    public EPM_Strategy_Loader ParentID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.ParentID, value);
        return this;
    }
    
    public EPM_Strategy_Loader ParentID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.ParentID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader ParentIDPreLoad(Long value, String preLoadTableName) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.ParentID, value, preLoadTableName);
        return this;
    }
    
    /**
     * 根据字段代码(Code)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader Code(String value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Code, value);
        return this;
    }
    
    public EPM_Strategy_Loader Code(String[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Code, value);
        return this;
    }
    
    public EPM_Strategy_Loader Code(String operator, String value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.Code, operator, value);
        return this;
    }
    
    /**
     * 根据字段集团(ClientID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader ClientID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.ClientID, value);
        return this;
    }
    
    public EPM_Strategy_Loader ClientID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.ClientID, value);
        return this;
    }
    
    public EPM_Strategy_Loader ClientID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.ClientID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader ClientIDPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.ClientID, value, "BK_Client"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段创建人员(Creator)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader Creator(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Creator, value);
        return this;
    }
    
    public EPM_Strategy_Loader Creator(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Creator, value);
        return this;
    }
    
    public EPM_Strategy_Loader Creator(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.Creator, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader CreatorPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.Creator, value, "SYS_Operator"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段修改人员(Modifier)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader Modifier(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Modifier, value);
        return this;
    }
    
    public EPM_Strategy_Loader Modifier(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Modifier, value);
        return this;
    }
    
    public EPM_Strategy_Loader Modifier(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.Modifier, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader ModifierPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.Modifier, value, "SYS_Operator"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段备注(Notes)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader Notes(String value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Notes, value);
        return this;
    }
    
    public EPM_Strategy_Loader Notes(String[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.Notes, value);
        return this;
    }
    
    public EPM_Strategy_Loader Notes(String operator, String value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.Notes, operator, value);
        return this;
    }
    
    /**
     * 根据字段计划标识(SchedulingIndicator)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader SchedulingIndicator(String value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.SchedulingIndicator, value);
        return this;
    }
    
    public EPM_Strategy_Loader SchedulingIndicator(String[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.SchedulingIndicator, value);
        return this;
    }
    
    public EPM_Strategy_Loader SchedulingIndicator(String operator, String value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.SchedulingIndicator, operator, value);
        return this;
    }
    
    /**
     * 根据字段策略单位(StrategyUnitID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader StrategyUnitID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.StrategyUnitID, value);
        return this;
    }
    
    public EPM_Strategy_Loader StrategyUnitID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.StrategyUnitID, value);
        return this;
    }
    
    public EPM_Strategy_Loader StrategyUnitID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.StrategyUnitID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader StrategyUnitIDPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.StrategyUnitID, value, "BK_Unit"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段调用期(CallHorizon)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader CallHorizon(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.CallHorizon, value);
        return this;
    }
    
    public EPM_Strategy_Loader CallHorizon(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.CallHorizon, value);
        return this;
    }
    
    public EPM_Strategy_Loader CallHorizon(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.CallHorizon, operator, value);
        return this;
    }
    
    /**
     * 根据字段工厂日历(FactoryCalendarID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader FactoryCalendarID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.FactoryCalendarID, value);
        return this;
    }
    
    public EPM_Strategy_Loader FactoryCalendarID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.FactoryCalendarID, value);
        return this;
    }
    
    public EPM_Strategy_Loader FactoryCalendarID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.FactoryCalendarID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader FactoryCalendarIDPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.FactoryCalendarID, value, "BK_Calendar"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段延迟完成时的替换因子(LateShiftFactor)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader LateShiftFactor(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.LateShiftFactor, value);
        return this;
    }
    
    public EPM_Strategy_Loader LateShiftFactor(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.LateShiftFactor, value);
        return this;
    }
    
    public EPM_Strategy_Loader LateShiftFactor(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.LateShiftFactor, operator, value);
        return this;
    }
    
    /**
     * 根据字段提早完成时的替换因子(EarlyShiftFactor)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader EarlyShiftFactor(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.EarlyShiftFactor, value);
        return this;
    }
    
    public EPM_Strategy_Loader EarlyShiftFactor(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.EarlyShiftFactor, value);
        return this;
    }
    
    public EPM_Strategy_Loader EarlyShiftFactor(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.EarlyShiftFactor, operator, value);
        return this;
    }
    
    /**
     * 根据字段延迟完成的容差(LateTolerance)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader LateTolerance(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.LateTolerance, value);
        return this;
    }
    
    public EPM_Strategy_Loader LateTolerance(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.LateTolerance, value);
        return this;
    }
    
    public EPM_Strategy_Loader LateTolerance(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.LateTolerance, operator, value);
        return this;
    }
    
    /**
     * 根据字段提早完成的容差(EarlyTolerance)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader EarlyTolerance(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.EarlyTolerance, value);
        return this;
    }
    
    public EPM_Strategy_Loader EarlyTolerance(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.EarlyTolerance, value);
        return this;
    }
    
    public EPM_Strategy_Loader EarlyTolerance(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.EarlyTolerance, operator, value);
        return this;
    }
    
    /**
     * 根据字段事务码(TCodeID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader TCodeID(Long value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TCodeID, value);
        return this;
    }
    
    public EPM_Strategy_Loader TCodeID(Long[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.TCodeID, value);
        return this;
    }
    
    public EPM_Strategy_Loader TCodeID(String operator, Long value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.TCodeID, operator, value);
        return this;
    }
    
    public EPM_Strategy_Loader TCodeIDPreLoad(Long value) throws Throwable {
        addMetaColumnValuePreLoad(EPM_Strategy.TCodeID, value, "EGS_TCode"); // 为了规避循环引用，直接使用字符串 
        return this;
    }
    
    /**
     * 根据字段是否周期集(IsCycleSet)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader IsCycleSet(int value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.IsCycleSet, value);
        return this;
    }
    
    public EPM_Strategy_Loader IsCycleSet(int[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.IsCycleSet, value);
        return this;
    }
    
    public EPM_Strategy_Loader IsCycleSet(String operator, int value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.IsCycleSet, operator, value);
        return this;
    }
    
    /**
     * 根据字段单据Key(SystemVestKey)的值进行过滤
     * 
     * @param value
     * @return
     */
    public EPM_Strategy_Loader SystemVestKey(String value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.SystemVestKey, value);
        return this;
    }
    
    public EPM_Strategy_Loader SystemVestKey(String[] value) throws Throwable {
        addMetaColumnValue(EPM_Strategy.SystemVestKey, value);
        return this;
    }
    
    public EPM_Strategy_Loader SystemVestKey(String operator, String value) throws Throwable {
        addMetaColumnValueObjectOperator(EPM_Strategy.SystemVestKey, operator, value);
        return this;
    }
    
    /**
     * 根据主键取单个表对象
     * 
     * @param primaryValue
     * @return
     * @throws Throwable
     */
    public EPM_Strategy load(Long primaryValue) throws Throwable {
        if (whereExpression != null) {
            throw new Exception("按主键取数前不得设置其他条件.");
        }
        OID(primaryValue);
        return loadNotNull();
    }
    
    @Override
    public EPM_Strategy load() throws Throwable {
        DataTable rst = EntityContext.findTableEntityData(context, this, EPM_Strategy.EPM_Strategy);
        if (rst == null) {
            return null;
        }
        return new EPM_Strategy(context, rst, rst.getLong(0, SystemField.OID_SYS_KEY), 0);
    }
    
    @Override
    public EPM_Strategy loadNotNull() throws Throwable {
        EPM_Strategy result = load();
        if (result == null) {
            throwTableEntityNotNullError(EPM_Strategy.class);
        }
        return result;
    }
    
    @Override
    public List<EPM_Strategy> loadList() throws Throwable {
        DataTable rst = EntityContext.findTableEntityDataList(context, this, orderBys, EPM_Strategy.EPM_Strategy);
        int length = rst == null ? 0 : rst.size();
        if (length == 0) {
            return null;
        }
        List<EPM_Strategy> resultList = new ArrayList<EPM_Strategy>(length);
        int oidColumnIndex = rst.getMetaData().findColumnIndexByKey(SystemField.OID_SYS_KEY);
        for (int rowIndex = 0; rowIndex < length; rowIndex++) {
            Long oid = rst.getLong(rowIndex, oidColumnIndex);
            EPM_Strategy tmp = new EPM_Strategy(context, rst, oid, rowIndex);
            resultList.add(tmp);
        }
        return resultList;
    }
    
    @Override
    public List<EPM_Strategy> loadListNotNull() throws Throwable {
        List<EPM_Strategy> result = loadList();
        if (result == null) {
            throwTableEntityListNotNullError(EPM_Strategy.class);
        }
        return result;
    }
    
    /**
    * 返回排序后的表对象
    * 
    * @return
    * @throws Throwable
    */
    public EPM_Strategy loadFirst() throws Throwable {
        List<EPM_Strategy> result = loadList();
        if (result == null) {
            return null;
        }
        return result.get(0);
    }
    
    /**
    * 返回排序后的表对象,如果返回空,报错
    * 
    * @return
    * @throws Throwable
    */
    public EPM_Strategy loadFirstNotNull() throws Throwable {
        List<EPM_Strategy> result = loadListNotNull();
        return result.get(0);
    }
    
    @Override
    public void delete() throws Throwable {
        EntityContext.deleteTableEntities(context, EPM_Strategy.class, this);
    }
    
    /**
    * 根据字段fieldKey排序
    * 
    * @param fieldKey
    * @return
    */
    public EPM_Strategy_Loader orderBy(String fieldKey) {
        super.orderBy(new OrderByExpression(EPM_Strategy.key2ColumnNames.get(fieldKey)));
        return this;
    }
    
    @Override
    public EPM_Strategy_Loader desc() {
        super.desc();
        return this;
    }
    
    @Override
    public EPM_Strategy_Loader asc() {
        super.asc();
        return this;
    }
}
