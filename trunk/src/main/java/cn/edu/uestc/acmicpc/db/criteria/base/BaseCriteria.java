package cn.edu.uestc.acmicpc.db.criteria.base;

import cn.edu.uestc.acmicpc.db.dto.FieldProjection;
import cn.edu.uestc.acmicpc.db.dto.Fields;
import cn.edu.uestc.acmicpc.util.exception.AppException;
import cn.edu.uestc.acmicpc.util.exception.AppExceptionUtil;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.Set;

/**
 * We can use this class to get {@link DetachedCriteria} entity.
 *
 * @param <E>
 *          entity type
 * @param <D>
 *          data transfer object type
 */
public abstract class BaseCriteria<E, D> {

  /**
   * Current page id.
   */
  public Long currentPage;

  /**
   * Number of records per page.
   */
  public Long countPerPage;

  /**
   * Fields for ordering, separated with ','.
   */
  public String orderFields;

  /**
   * Whether ordered by ascending order for each fields, separated with ','.
   * Each value can be {@code true} or {@code false}.
   */
  public String orderAsc;

  /**
   * Reference class
   */
  private final Class<E> referenceClass;

  /**
   * Result class, this class should generate by protocol buffer
   */
  private final Class<D> resultClass;

  /**
   * Specify the fields we need when build query criteria
   */
  private Set<Fields> resultFields;

  protected BaseCriteria(Class<E> referenceClass,
      Class<D> resultClass,
      Set<Fields> resultFields) {
    this.referenceClass = referenceClass;
    this.resultClass = resultClass;
    this.resultFields = resultFields;
  }

  public void setResultFields(Set<Fields> resultFields) {
    this.resultFields = resultFields;
  }

  /**
   * Get {@link DetachedCriteria} object.
   *
   * @return {@link DetachedCriteria} object we need.
   * @throws AppException
   */
  public DetachedCriteria getCriteria() throws AppException {
    DetachedCriteria criteria = DetachedCriteria.forClass(referenceClass);

    if (resultFields != null) {
      ProjectionList projectionList = Projections.projectionList();
      for (Fields field : resultFields) {
        FieldProjection fieldProjection = field.getProjection();
        if (fieldProjection.getType() == FieldProjection.ProjectionType.ALIAS) {
          // Set alias
          criteria.createAlias(fieldProjection.getField(), fieldProjection.getAlias());
        } else if (fieldProjection.getType() == FieldProjection.ProjectionType.DB_FIELD) {
          // Set projection
          projectionList.add(Projections.property(fieldProjection.getField()),
              fieldProjection.getAlias());
        }
      }
      criteria.setProjection(projectionList);
    }

    // Set result transformer
    // We must set the projection first
    // setProjection() method will change the transformer to PROJECTION
    criteria = criteria.setResultTransformer(new AliasToBeanResultTransformer(resultClass));

    // Set order condition
    if (orderFields != null) {
      String[] fields = orderFields.split(",");
      String[] asc = orderAsc.split(",");
      AppExceptionUtil.assertTrue(fields.length == asc.length);
      for (int i = 0; i < fields.length; i++) {
        if (asc[i].equals("true")) {
          criteria.addOrder(Order.asc(fields[i]));
        } else {
          criteria.addOrder(Order.desc(fields[i]));
        }
      }
    }
    return criteria;
  }

}
