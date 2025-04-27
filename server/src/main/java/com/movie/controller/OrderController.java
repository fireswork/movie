package com.movie.controller;

import com.movie.entity.Order;
import com.movie.entity.Movie;
import com.movie.entity.UserMovie;
import com.movie.repository.OrderRepository;
import com.movie.repository.MovieRepository;
import com.movie.repository.UserMovieRepository;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserMovieRepository userMovieRepository;

    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody Order orderRequest) {
        // 检查电影是否存在且为付费电影
        Optional<Movie> movieOpt = movieRepository.findById(orderRequest.getMovieId());
        if (movieOpt.isEmpty()) {
            return ApiResponse.error(400, "电影不存在");
        }
        
        Movie movie = movieOpt.get();
        if (movie.getIsFree()) {
            return ApiResponse.error(400, "免费电影无需购买");
        }

        // 检查是否已有未支付订单
        Optional<Order> existingOrder = orderRepository.findByUserIdAndMovieIdAndStatus(
            orderRequest.getUserId(), orderRequest.getMovieId(), "PENDING");
        if (existingOrder.isPresent()) {
            return ApiResponse.success(existingOrder.get(), "已存在待支付订单");
        }

        // 检查是否已购买且未过期
        Optional<UserMovie> userMovie = userMovieRepository.findByUserIdAndMovieIdAndExpiredAtGreaterThan(
            orderRequest.getUserId(), orderRequest.getMovieId(), LocalDateTime.now());
        if (userMovie.isPresent()) {
            return ApiResponse.error(400, "已购买该电影且未过期");
        }

        // 验证订单金额是否正确
        if (!orderRequest.getAmount().equals(movie.getPrice())) {
            return ApiResponse.error(400, "订单金额不正确");
        }

        // 创建新订单
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setMovieId(orderRequest.getMovieId());
        order.setAmount(orderRequest.getAmount());
        order.setStatus("PENDING");
        
        Order savedOrder = orderRepository.save(order);
        return ApiResponse.success(savedOrder, "订单创建成功");
    }

    @PostMapping("/{orderId}/pay")
    public ApiResponse<Order> payOrder(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ApiResponse.error(400, "订单不存在");
        }

        Order order = orderOpt.get();
        if ("PAID".equals(order.getStatus())) {
            return ApiResponse.error(400, "订单已支付");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            return ApiResponse.error(400, "订单已取消");
        }

        // 创建用户电影购买记录
        UserMovie userMovie = new UserMovie();
        userMovie.setUserId(order.getUserId());
        userMovie.setMovieId(order.getMovieId());
        userMovie.setExpiredAt(LocalDateTime.now().plusHours(24));
        userMovieRepository.save(userMovie);

        // 更新订单状态
        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);
        return ApiResponse.success(updatedOrder, "支付成功");
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> checkPurchaseStatus(@RequestParam Long userId, @RequestParam Long movieId) {
        // 先检查电影是否免费
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            return ApiResponse.error(400, "电影不存在");
        }
        
        Movie movie = movieOpt.get();
        if (movie.getIsFree()) {
            return ApiResponse.success(true, "免费电影");
        }

        // 检查是否已购买且未过期
        Optional<UserMovie> userMovie = userMovieRepository.findByUserIdAndMovieIdAndExpiredAtGreaterThan(
            userId, movieId, LocalDateTime.now());
        return ApiResponse.success(userMovie.isPresent(), userMovie.isPresent() ? "已购买且未过期" : "未购买或已过期");
    }
} 