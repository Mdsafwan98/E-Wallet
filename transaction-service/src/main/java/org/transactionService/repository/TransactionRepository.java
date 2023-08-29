package org.transactionService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.transactionService.model.Transaction;
import org.transactionService.model.TransactionStatus;

import javax.transaction.Transactional;

/**
 * This class is used as a repository for Transaction API.
 *
 * @author safwanmohammed907@gmal.com
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    //Update transaction status when transaction is initiated.
    @Transactional
    @Modifying
    @Query("update Transaction t set t.transactionStatus= ?2  where t.externalId= ?1")
    void updateTransaction(String externalTxnId, TransactionStatus transactionStatus);
}
