package com.loganjia.store.repositories;


import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional()
public abstract class AbstractJpaTest extends AbstractIntegrationTest {

    @Autowired
    protected EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // 初始化每个测试的数据
        // 清空表
        entityManager.createNativeQuery("DELETE FROM product").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM category").executeUpdate();

        // 插入测试数据
        entityManager.createNativeQuery("INSERT INTO category(id, name) VALUES (1, 'Electronics')").executeUpdate();
        entityManager.createNativeQuery("INSERT INTO category(id, name) VALUES (2, 'Books')").executeUpdate();

        entityManager.createNativeQuery(
                "INSERT INTO product(id, name, price, category_id) VALUES " +
                        "(1, 'iPhone 13', 999.99, 1), " +
                        "(2, 'MacBook Pro', 1999.99, 1), " +
                        "(3, 'Spring in Action', 39.99, 2), " +
                        "(4, 'Effective Java', 49.99, 2), " +
                        "(5, 'iPad Air', 599.99, 1)"
        ).executeUpdate();
        entityManager.flush();
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }

    @AfterEach
    void tearDown() {
        // 清理数据
    }
}