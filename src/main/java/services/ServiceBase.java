package services;

import javax.persistence.EntityManager;

import utils.DBUtil;

/*
 * BD接続に関わる共通処理を行うクラス
 */
public class ServiceBase {

    /**
     * EntityManagerインスタンス
     */
    protected EntityManager em = DBUtil.createEntityManager();

    /**
     * EntityManagerのクローズ
     */
    public void close() {
        if(em.isOpen()) {
            em.close();
        }
    }

}
