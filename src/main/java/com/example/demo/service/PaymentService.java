package com.example.demo.service;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.razorpay.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private UserRepository userRepo;

    // 🔐 Move keys to application.properties
    @Value("${razorpay.key}")
    private String KEY;

    @Value("${razorpay.secret}")
    private String SECRET;

    // 🔥 store orderId → amount mapping (temporary in-memory)
    private Map<String, Double> orderAmountMap = new HashMap<>();

    // ===========================
    // 🔥 CREATE ORDER
    // ===========================
    public String createOrder(double amount) throws Exception {

        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        JSONObject options = new JSONObject();
        options.put("amount", (int) (amount * 100)); // paisa
        options.put("currency", "INR");

        String receiptId = "txn_" + System.currentTimeMillis();
        options.put("receipt", receiptId);

        Order order = client.orders.create(options);

        // ✅ store amount against orderId
        orderAmountMap.put(order.get("id"), amount);

        return order.toString();
    }

    // ===========================
    // 🔥 VERIFY PAYMENT
    // ===========================
    @Transactional
    public String verifyPayment(PaymentRequest req, String username) {
    	System.out.println("VERIFY API HIT 🔥");

        // 🔐 1. VERIFY SIGNATURE
        boolean isValid = verifySignature(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature()
        );

        if (!isValid) {
            throw new RuntimeException("Invalid payment ❌");
        }

        // 👤 2. GET USER
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 💰 3. GET REAL AMOUNT
        Double amount = orderAmountMap.get(req.getRazorpayOrderId());

        if (amount == null) {
            throw new RuntimeException("Amount not found for order ❌");
        }


        // 💰 4. UPDATE BALANCE
        user.setBalance(user.getBalance() + amount);
        userRepo.save(user);

        // 🧾 5. SAVE TRANSACTION
        Transaction txn = new Transaction();
        txn.setUsername(username);
        txn.setAmount(amount);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setDescription("Razorpay payment");

        transactionRepo.save(txn);

        // 🧹 optional cleanup
//        orderAmountMap.remove(req.getRazorpayOrderId());

        return "Payment verified & wallet updated";
    }

    // ===========================
    // 🔐 SIGNATURE VERIFICATION
    // ===========================
    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes());

            // 🔥 CONVERT TO HEX (NOT BASE64)
            StringBuilder hex = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hexByte = Integer.toHexString(0xff & b);
                if (hexByte.length() == 1) hex.append('0');
                hex.append(hexByte);
            }

            String generatedSignature = hex.toString();

            System.out.println("EXPECTED: " + signature);
            System.out.println("GENERATED: " + generatedSignature);

            return generatedSignature.equals(signature);

        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed");
        }
    }
}