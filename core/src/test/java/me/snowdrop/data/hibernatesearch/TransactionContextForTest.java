package me.snowdrop.data.hibernatesearch;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.hibernate.search.backend.TransactionContext;

/**
 * @author Emmanuel Bernard
 * @author Ales Justin
 */
public class TransactionContextForTest implements TransactionContext {
  private boolean progress = true;
  private List<Synchronization> syncs = new ArrayList<>();

  @Override
  public boolean isTransactionInProgress() {
    return progress;
  }

  @Override
  public Object getTransactionIdentifier() {
    return this;
  }

  @Override
  public void registerSynchronization(Synchronization synchronization) {
    syncs.add(synchronization);
  }

  public void end() {
    this.progress = false;

    for (Synchronization sync : syncs) {
      sync.beforeCompletion();
    }

    for (Synchronization sync : syncs) {
      sync.afterCompletion(Status.STATUS_COMMITTED);
    }
  }
}