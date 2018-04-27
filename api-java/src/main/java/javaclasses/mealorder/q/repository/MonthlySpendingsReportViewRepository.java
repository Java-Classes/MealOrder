package javaclasses.mealorder.q.repository;

import io.spine.server.projection.ProjectionRepository;
import io.spine.server.route.EventRouting;
import io.spine.time.MonthOfYear;
import javaclasses.mealorder.LocalMonth;
import javaclasses.mealorder.MonthlySpendingsReportId;
import javaclasses.mealorder.c.event.PurchaseOrderDelivered;
import javaclasses.mealorder.c.event.PurchaseOrderSent;
import javaclasses.mealorder.q.MonthlySpendingsReportViewProjection;
import javaclasses.mealorder.q.projection.MonthlySpendingsReportView;

import static java.util.Collections.singleton;

public class MonthlySpendingsReportViewRepository extends ProjectionRepository<MonthlySpendingsReportId, MonthlySpendingsReportViewProjection, MonthlySpendingsReportView> {

    public MonthlySpendingsReportViewRepository() {
        super();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     *
     * <p>Override this method in successor classes, otherwise all successors will use
     * {@code MyListViewProjection.ID}.
     */
    protected void setUpEventRoute() {

        final EventRouting<MonthlySpendingsReportId> routing = getEventRouting();
        routing.route(PurchaseOrderSent.class,
                      (message, context) -> {
                          final PurchaseOrderSent purchaseOrderSent = (PurchaseOrderSent) message;
                          final int year = purchaseOrderSent.getPurchaseOrder()
                                                            .getId()
                                                            .getPoDate()
                                                            .getYear();
                          final MonthOfYear month = purchaseOrderSent.getPurchaseOrder()
                                                                     .getId()
                                                                     .getPoDate()
                                                                     .getMonth();
                          final LocalMonth localMonth = LocalMonth.newBuilder()
                                                                  .setYear(year)
                                                                  .setMonth(month)
                                                                  .build();
                          final MonthlySpendingsReportId monthlySpendingsReportId = MonthlySpendingsReportId.newBuilder()
                                                                                                            .setMonth(
                                                                                                                    localMonth)
                                                                                                            .build();
                          return singleton(monthlySpendingsReportId);
                      });
        routing.route(PurchaseOrderDelivered.class,
                      (message, context) -> {
                          final PurchaseOrderDelivered purchaseOrderDelivered = (PurchaseOrderDelivered) message;
                          final int year = purchaseOrderDelivered.getId()
                                                                 .getPoDate()
                                                                 .getYear();
                          final MonthOfYear month = purchaseOrderDelivered.getId()
                                                                          .getPoDate()
                                                                          .getMonth();
                          final LocalMonth localMonth = LocalMonth.newBuilder()
                                                                  .setYear(year)
                                                                  .setMonth(month)
                                                                  .build();
                          final MonthlySpendingsReportId monthlySpendingsReportId = MonthlySpendingsReportId.newBuilder()
                                                                                                            .setMonth(
                                                                                                                    localMonth)
                                                                                                            .build();
                          return singleton(monthlySpendingsReportId);
                      });
    }
}
