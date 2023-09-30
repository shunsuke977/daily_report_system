package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Like;
import models.Report;
import models.validators.ReportValidator;

/**
 * 日報テーブルの操作に関わる処理を行うクラス
 */
public class ReportService extends ServiceBase {

    /**
     * 指定した従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得しReportViewのリストで返却する
     * @param employee 従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReportView> getMinePerPage(EmployeeView employee, int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 指定した従業員が作成した日報データの件数を取得し、返却する
     * @param employee
     * @return 日報データの件数
     */
    public long countAllMine(EmployeeView employee) {

        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    /**
     * 指定されたページ数の一覧画面に表示する日報データを取得し、ReportViewのリストで返却する
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReportView> getAllPerPage(int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 日報テーブルのデータの件数を取得し、返却する
     * @return データの件数
     */
    public long countAll() {
        long reports_count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT, Long.class)
                .getSingleResult();
        return reports_count;
    }

    /**
     * idを条件に取得したデータをReportViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    /**
     * 画面から入力された日報の登録内容を元にデータを1件作成し、日報テーブルに登録する
     * @param rv 日報の登録内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> create(ReportView rv) {
        List<String> errors = ReportValidator.validate(rv);
        if (errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }

        //バリデーションで発生したエラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * 画面から入力された日報の登録内容を元に、日報データを更新する
     * @param rv 日報の更新内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> update(ReportView rv) {

        //バリデーションを行う
        List<String> errors = ReportValidator.validate(rv);

        if (errors.size() == 0) {

            //更新日時を現在時刻に設定
            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }

        //バリデーションで発生したエラーを返却（エラーがなければ0件の空リスト）
        return errors;
    }

    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    /**
     * 日報データを1件登録する
     * @param rv 日報データ
     */
    private void createInternal(ReportView rv) {

        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));
        em.getTransaction().commit();

    }

    /**
     * 日報データを更新する
     * @param rv 日報データ
     */
    private void updateInternal(ReportView rv) {

        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();

    }

    /**
     * いいねデータを1件登録する
     * @param rv 日報データ
     * @param ev 従業員データ
     */
    public void createLike(ReportView rv, EmployeeView ev) {
        em.getTransaction().begin();
        Like l = new Like(
                null,
                rv.getId(),
                ev.getId());
        em.persist(l);
        em.getTransaction().commit();
    }

    /**
     * 日報id、従業員idを条件に取得したいいねデータを返却する
     * @param report 日報
     * @param employee 従業員
     * @return 取得データのインスタンス 取得できない場合null
     */
    public Like findOne(ReportView rv, EmployeeView ev) {
        Like l = null;
        try {
            l = em.createNamedQuery(JpaConst.Q_LIKE_GET_BY_REP_AND_EMP, Like.class)
                    .setParameter(JpaConst.JPQL_PARM_REPORT_ID, rv.getId())
                    .setParameter(JpaConst.JPQL_PARM_EMPLOYEE_ID, ev.getId())
                    .getSingleResult();

        } catch (NoResultException ex) {
        }

        return l;

    }

    /**
     * 指定したいいねデータを物理削除する
     * @param id
     */
    public void delete(Like l) {
        em.getTransaction().begin();
        em.remove(l);
        em.getTransaction().commit();
    }

    /**
     * 指定した日報データのいいねの件数を取得し、返却する
     * @param report
     * @return いいねの件数
     */
    public long countLike(ReportView rv) {

        long count = (long) em.createNamedQuery(JpaConst.Q_LIKE_COUNT_REGISTERED_BY_REPORT_ID, Long.class)
                .setParameter(JpaConst.JPQL_PARM_REPORT_ID, rv.getId())
                .getSingleResult();

        return count;
    }

    /**
     * 指定した従業員がいいねした日報データを、指定されたページ数の「いいねした日報」一覧画面に表示する分取得し、ReportViewのリストで返却する
     * @param ev 従業員データ
     * @param page ページ数
     * @return 「いいねした日報」一覧画面に表示するデータのリスト
     */
    public List<ReportView> getMyFavoriteReportsPerPage(EmployeeView ev, int page) {
        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_BY_LIKE_EMP_ID, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE_ID, ev.getId())
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 指定した従業員がいいねした日報データをの数を返却する
     * @param ev 従業員データ
     * @return いいねした日報データの数
     */
    public long countMyFavoriteReports(EmployeeView ev) {
        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_BY_LIKE_EMP_ID, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE_ID, ev.getId())
                .getResultList();
        return (long)reports.size();
    }

}