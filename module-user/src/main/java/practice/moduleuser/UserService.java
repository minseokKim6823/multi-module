//package practice.notepad;
//
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    @Cacheable(cacheNames = "users", key = "#id")
//    public String getUser(Long id) {
//        System.out.println("DB 조회 수행: " + id);
//        return "User-" + id; // 실제로는 DB에서 가져오는 로직
//    }
//}
//
