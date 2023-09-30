package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * いいねデータのDTOモデル
 *
 */
@Table(name = JpaConst.TABLE_LIKE)
@NamedQueries({
    @NamedQuery(
            name = JpaConst.Q_LIKE_COUNT_REGISTERED_BY_REPORT_ID,
            query = JpaConst.Q_LIKE_COUNT_REGISTERED_BY_REPORT_ID_DEF),
    @NamedQuery(
            name = JpaConst.Q_LIKE_GET_BY_REP_AND_EMP,
            query = JpaConst.Q_LIKE_GET_BY_REP_AND_EMP_DEF),
})
@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
@Entity
public class Like {

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.LIKE_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 登録された日報のid
     */
    @Column(name = JpaConst.LIKE_COL_REP, nullable = false)
    private Integer reportId;

    /**
     * 日報を登録した従業員のid
     */
    @Column(name = JpaConst.LIKE_COL_EMP, nullable = false)
    private Integer employeeId;

}
