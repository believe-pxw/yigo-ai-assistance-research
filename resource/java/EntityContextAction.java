package com.bokesoft.erp.entity.util;

import java.util.List;

import com.bokesoft.entity.BaseContextAction;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument;
import com.bokesoft.yigo.struct.env.Env;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;
import com.bokesoft.yes.mid.parameterizedsql.SqlString;
import com.bokesoft.yigo.struct.datatable.DataTable;

/**
 * 带实体管理功能的中间层二次开发基类,包含了com.bokesoft.yigo.entity.ITableEntityManager和com.
 * bokesoft.yigo.entity.TableEntityManagerMid这两个接口的方法.<br>
 * 为什么不直接实现这两个接口,因为这两个接口中的find()因为泛型的原因重复,编译不通过.
 */
public class EntityContextAction extends BaseContextAction {
	protected RichDocumentContext _context;

	public EntityContextAction(RichDocumentContext _context) {
		this._context = _context;
	}

	public RichDocumentContext getMidContext() {
		return this._context;
	}

	public Env getEnv() throws Throwable {
		return _context.getEnv();
	}

	public Long getClientID() throws Throwable {
		return _context.getClientID();
	}

	public Long getUserID() throws Throwable {
		return _context.getEnv().getUserID();
	}

	/**
	 * 保存表单实体 <br>
	 * 中间层大数据计算慎用
	 * 
	 * @param entity
	 * @throws Throwable
	 */
	public void save(AbstractBillEntity entity) throws Throwable {
		EntityContext.save(entity);
	}

	public void directSave(AbstractBillEntity entity) throws Throwable {
		EntityContext.directSave(entity);
	}

	/**
	 * 保存表单实体,使用表达式去保存
	 * 
	 * @param entity
	 * @param saveFormula
	 * @throws Throwable
	 */
	public void save(AbstractBillEntity entity, String saveFormula) throws Throwable {
		EntityContext.save(entity, saveFormula);
	}

	/**
	 * 删除表单实体
	 * 
	 * @param entity
	 * @throws Throwable
	 */
	public void directDelete(AbstractBillEntity entity) throws Throwable {
		EntityContext.directDelete(entity);
	}
    public void delete(AbstractBillEntity entity) throws Throwable {
        EntityContext.delete(entity);
    }
    public void delete(AbstractBillEntity entity, String deleteFormula) throws Throwable {
        EntityContext.delete(entity, deleteFormula);
    }

	/**
	 * 克隆表单实体 但是不锁定字段值
	 * @param entity
	 * @param callback
	 * @return
	 * @param <T>
	 * @throws Throwable
	 */
	public <T extends AbstractBillEntity> T cloneBill(T entity, IEntityCallback callback)
			throws Throwable {
		return  EntityContext.cloneBill(_context, entity, callback);
	}

	/**
	 * 克隆表单实体,并锁定字段值
	 * @param entity
	 * @param callback
	 * @return
	 * @param <T>
	 * @throws Throwable
	 */
	public <T extends AbstractBillEntity> T cloneBillAndLockValue(T entity, IEntityCallback callback)
			throws Throwable {
		return EntityContext.cloneBillAndLockValue(_context, entity, callback);
	}
	
	public <T extends AbstractBillEntity> T newBillEntity(Class<T> entityClass) throws Throwable {
		return newBillEntity(entityClass, true, null);
	}

	/**
	 * 新建表单实体
	 *
	 * @param entityClass
	 * @return
	 * @throws Throwable
	 */
	public <T extends AbstractBillEntity> T newBillEntity(Class<T> entityClass, boolean runDefaultValue) throws Throwable {
		return EntityContext.newBillEntity(_context, entityClass,runDefaultValue, null);
	}
	
	/**
	 * 新建表单实体，根据参数决定是否有父Document
	 * @param entityClass
	 * @param parentFormKey 父界面的Key，若当前Context中Document的FormKey相同，那将此Docuemnt设置为新表单实体的父Document，否则新表单实体无父Document(为null)，用于默认值中parent.xxx的计算，若指定错误的父Document，可能会报xxx字段不存在
	 * @return
	 * @throws Throwable
	 */
	public <T extends AbstractBillEntity> T newBillEntity(Class<T> entityClass, String parentFormKey) throws Throwable {
		return newBillEntity(entityClass, true, parentFormKey);
	}

	/**
	 * 新建表单实体
	 * @param entityClass
	 * @param runDefaultValue
	 * @param parentFormKey 父界面的Key，若当前Context中Document的FormKey相同，那将此Docuemnt设置为新表单实体的父Document，否则新表单实体无父Document(为null)，用于默认值中parent.xxx的计算，若指定错误的父Document，可能会报xxx字段不存在
	 * @param <T>
	 * @return
	 * @throws Throwable
	 */
	public <T extends AbstractBillEntity> T newBillEntity(Class<T> entityClass, boolean runDefaultValue, String parentFormKey) throws Throwable {
		return EntityContext.newBillEntity(_context, entityClass,runDefaultValue, parentFormKey);
	}

	/**
	 * 取表单数据对象
	 * 
	 * @return
	 * @throws Throwable
	 */
	public RichDocument getDocument() throws Throwable {
		return getMidContext().getRichDocument();
	}
	public RichDocument getRichDocument() throws Throwable {
		return getMidContext().getRichDocument();
	}

	/**
	 * 针对表实体计算表达式
	 * 
	 * @param expression
	 * @param description
	 * @return
	 * @throws Throwable
	 */
	public Object evalFormula(String expression, String description) throws Throwable {
		return getMidContext().evalFormula(expression, description);
	}

	/**
	 * 删除单个表实体对象,没有考虑行锁
	 * 
	 * @param tableEntity
	 * @throws Throwable
	 */
	public void delete(AbstractTableEntity tableEntity, String formKey) throws Throwable {
		EntityContext.delete(_context, tableEntity, formKey);
	}

	/**
	 * 保存单个表实体对象,没有考虑行锁
	 * 
	 * @param tableEntity
	 * @throws Throwable
	 */
	public void save(AbstractTableEntity tableEntity, String formKey) throws Throwable {
		EntityContext.save(_context, tableEntity, formKey);
	}

	/**
	 * 保存多个表实体对象,没有考虑行锁
	 * 
	 * @param tableEntities
	 * @throws Throwable
	 */
	public void save(List<? extends AbstractTableEntity> tableEntities, String formKey) throws Throwable {
		EntityContext.save(_context, tableEntities, formKey);
	}


	public DataTable getResultSet(SqlString sql) throws Throwable {
		return EntityContext.getRowSet(_context, sql);
	}

	public DataTable getPrepareResultSet(String sql, Object[] arguments) throws Throwable {
		return EntityContext.getPrepareResultSet(_context, sql, arguments);
	}


	public void executeSQL(SqlString sql) throws Throwable {
		EntityContext.executeSQL(_context, sql);
	}
}
