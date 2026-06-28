package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Provider;

import com.example.demo.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	
	 	@Query("SELECT SUM(o.sellPrice) FROM OrderEntity o WHERE o.status = 'COMPLETED'")
	    Double getTotalRevenue();

	    @Query("SELECT SUM(o.costPrice) FROM OrderEntity o WHERE o.status = 'COMPLETED'")
	    Double getTotalCost();

	    @Query("SELECT SUM(o.profit) FROM OrderEntity o WHERE o.status = 'COMPLETED'")
	    Double getTotalProfit();

	    @Query("SELECT COUNT(o) FROM OrderEntity o")
	    Long getTotalOrders();

	    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.status = 'COMPLETED'")
	    Long getCompletedOrders();

	    @Query("""
	        SELECT SUM(o.profit) FROM OrderEntity o
	        WHERE o.status = 'COMPLETED'
	        AND DATE(o.createdAt) = CURRENT_DATE
	    """)
	    Double getTodayProfit();

	    @Query("""
	        SELECT o.service.name, SUM(o.profit)
	        FROM OrderEntity o
	        WHERE o.status = 'COMPLETED'
	        GROUP BY o.service.name
	        ORDER BY SUM(o.profit) DESC
	    """)
	    List<Object[]> getTopServices();

	    @Query("""
	        SELECT o.provider.name, COUNT(o), SUM(o.profit)
	        FROM OrderEntity o
	        WHERE o.status = 'COMPLETED'
	        GROUP BY o.provider.name
	    """)
	    List<Object[]> getProviderStats();
	    Page<OrderEntity> findByCustomerName(String customerName, Pageable pageable);
	long countByProvider(Provider provider);
	
	List<OrderEntity> findByProviderAndStatusIn(
        Provider provider,
        List<String> statuses
);
