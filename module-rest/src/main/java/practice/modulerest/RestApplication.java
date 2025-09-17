package practice.modulerest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import practice.modulecommon.TestBean;

@SpringBootApplication(
        scanBasePackages = {
                "practice.modulerest",
                "practice.moduleuser",      // 컨트롤러/서비스
                "practice.modulecommon"     // 공통 빈들 있으면
        }
)
@EnableJpaRepositories(basePackages = {
        "practice.moduleuser",         // @Entity(User 등)
        "practice.modulecommon"
})
@EntityScan(basePackages = {
        "practice.moduleuser",         // @Entity(User 등)
        "practice.modulecommon"
})
public class RestApplication {
    private final TestBean testBean;

    @Autowired
    public RestApplication(TestBean testBean) {
        this.testBean = testBean;
    }

    @PostConstruct
    public void dependencyTest() {
        testBean.dependencyTest();
    }
    // 의존성 확인을 위한 코드 - 끝

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
