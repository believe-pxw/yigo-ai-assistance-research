/* YigoCAD工具生成,这个文件会被重新生成,请不要修改这个文件,也不要创建同名的文件. */
/* 生成时间 2026-01-04 09:54:18 */
package com.bokesoft.erp.billentity.pmconfig;

import com.bokesoft.erp.entity.util.AbstractBillLoader;
import com.bokesoft.erp.entity.util.EntityContext;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;

/**
 * 维护策略(PM_Strategy)加载器<br>
 * 包含如下表实体:EPM_Strategy<br>
 * EPM_StrategyDtl<br>
 * 
 * @author ERP研发部
 */
public class PM_Strategy_Loader extends AbstractBillLoader<PM_Strategy_Loader> {
 
    protected PM_Strategy_Loader(RichDocumentContext context) throws Throwable {
        super(context, PM_Strategy.PM_Strategy);
    }
    
    /**
     * 根据字段周期集(IsCycleSet)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader IsCycleSet(int value) throws Throwable {
        addFieldValue(PM_Strategy.IsCycleSet, value);
        return this;
    }
    
    /**
     * 根据字段制单人(Creator)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Creator(Long value) throws Throwable {
        addFieldValue(PM_Strategy.Creator, value);
        return this;
    }
    
    /**
     * 根据字段制单日期(CreateDate)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader CreateDate(Long value) throws Throwable {
        addFieldValue(PM_Strategy.CreateDate, value);
        return this;
    }
    
    /**
     * 根据字段SOID(SOID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader SOID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.SOID, value);
        return this;
    }
    
    /**
     * 根据字段调用期(CallHorizon)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader CallHorizon(int value) throws Throwable {
        addFieldValue(PM_Strategy.CallHorizon, value);
        return this;
    }
    
    /**
     * 根据字段事务码(TCodeID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader TCodeID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.TCodeID, value);
        return this;
    }
    
    /**
     * 根据字段工厂日历(FactoryCalendarID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader FactoryCalendarID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.FactoryCalendarID, value);
        return this;
    }
    
    /**
     * 根据字段状态(Enable)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Enable(int value) throws Throwable {
        addFieldValue(PM_Strategy.Enable, value);
        return this;
    }
    
    /**
     * 根据字段策略单位(StrategyUnitID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader StrategyUnitID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.StrategyUnitID, value);
        return this;
    }
    
    /**
     * 根据字段修改人(Modifier)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Modifier(Long value) throws Throwable {
        addFieldValue(PM_Strategy.Modifier, value);
        return this;
    }
    
    /**
     * 根据字段提早完成时的替换因子(EarlyShiftFactor)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader EarlyShiftFactor(int value) throws Throwable {
        addFieldValue(PM_Strategy.EarlyShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段代码(Code)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Code(String value) throws Throwable {
        addFieldValue(PM_Strategy.Code, value);
        return this;
    }
    
    /**
     * 根据字段延迟完成的容差(LateTolerance)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader LateTolerance(int value) throws Throwable {
        addFieldValue(PM_Strategy.LateTolerance, value);
        return this;
    }
    
    /**
     * 根据字段提早完成的容差(EarlyTolerance)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader EarlyTolerance(int value) throws Throwable {
        addFieldValue(PM_Strategy.EarlyTolerance, value);
        return this;
    }
    
    /**
     * 根据字段节点类型(NodeType)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader NodeType(int value) throws Throwable {
        addFieldValue(PM_Strategy.NodeType, value);
        return this;
    }
    
    /**
     * 根据字段延迟完成时的替换因子(LateShiftFactor)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader LateShiftFactor(int value) throws Throwable {
        addFieldValue(PM_Strategy.LateShiftFactor, value);
        return this;
    }
    
    /**
     * 根据字段系统环境(ClientID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader ClientID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.ClientID, value);
        return this;
    }
    
    /**
     * 根据字段计划标识(SchedulingIndicator)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader SchedulingIndicator(String value) throws Throwable {
        addFieldValue(PM_Strategy.SchedulingIndicator, value);
        return this;
    }
    
    /**
     * 根据字段维护包(Dtl_PackageNo)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_PackageNo(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_PackageNo, value);
        return this;
    }
    
    /**
     * 根据字段单位(Dtl_PackageUnitID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_PackageUnitID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_PackageUnitID, value);
        return this;
    }
    
    /**
     * 根据字段周期长度(Dtl_CycleLength)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_CycleLength(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_CycleLength, value);
        return this;
    }
    
    /**
     * 根据字段偏置(Dtl_OffsetPos)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_OffsetPos(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_OffsetPos, value);
        return this;
    }
    
    /**
     * 根据字段周期短文本(Dtl_CycleShortText)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_CycleShortText(String value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_CycleShortText, value);
        return this;
    }
    
    /**
     * 根据字段层次(Dtl_CycleHierarchy)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_CycleHierarchy(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_CycleHierarchy, value);
        return this;
    }
    
    /**
     * 根据字段周期文本(Dtl_CycleNotes)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_CycleNotes(String value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_CycleNotes, value);
        return this;
    }
    
    /**
     * 根据字段选择(Dtl_IsSelect)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_IsSelect(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_IsSelect, value);
        return this;
    }
    
    /**
     * 根据字段后续(Dtl_FollowUpDays)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_FollowUpDays(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_FollowUpDays, value);
        return this;
    }
    
    /**
     * 根据字段偏置短文本(Dtl_OffsetShortText)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_OffsetShortText(String value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_OffsetShortText, value);
        return this;
    }
    
    /**
     * 根据字段初始(Dtl_LeadDays)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_LeadDays(int value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_LeadDays, value);
        return this;
    }
    
    /**
     * 根据字段(Dtl_OID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader Dtl_OID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.Dtl_OID, value);
        return this;
    }
    
    /**
     * 根据字段POID(POID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader POID(Long value) throws Throwable {
        addFieldValue(PM_Strategy.POID, value);
        return this;
    }
    
    /**
     * 根据字段VERID(VERID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader VERID(int value) throws Throwable {
        addFieldValue(PM_Strategy.VERID, value);
        return this;
    }
    
    /**
     * 根据字段DVERID(DVERID)的值进行过滤
     * 
     * @param value
     * @return
     */
    public PM_Strategy_Loader DVERID(int value) throws Throwable {
        addFieldValue(PM_Strategy.DVERID, value);
        return this;
    }
    
    /**
     * 根据主键取单个表单对象
     * 
     * @param primaryValue
     * 
     * @return
     * @throws Throwable
     */
    public PM_Strategy load(Long primaryValue) throws Throwable {
        if (whereExpression != null) {
            throw new Exception("按主键取数前不得设置其他条件.");
        }
        PM_Strategy result = EntityContext.findBillEntity(context, PM_Strategy.class, primaryValue);
        if (result == null) {
            throwBillEntityNotNullError(PM_Strategy.class, primaryValue);
        }
        return result;
    }
    
    /**
     * 根据代码取单个表单对象
     * 
     * @param code
     * @return
     * @throws Throwable
     */
    public PM_Strategy loadByCode(String code) throws Throwable {
        if (whereExpression != null) {
            throw new Exception("按主键取数前不得设置其他条件.");
        }
        PM_Strategy result = EntityContext.findBillEntityByCode(context, PM_Strategy.class, code);
        if (result == null) {
            addFieldValue(PM_Strategy.Code, code);
            throwBillEntityNotNullError(PM_Strategy.class);
        }
        return result;
    }
    
    @Override
    public PM_Strategy load() throws Throwable {
        return EntityContext.findBillEntity(context, PM_Strategy.class, this);
    }
    
    @Override
    public PM_Strategy loadNotNull() throws Throwable {
        PM_Strategy result = load();
        if (result == null) {
            throwBillEntityNotNullError(PM_Strategy.class);
        }
        return result;
    }
    
}