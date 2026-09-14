package com.aicoding.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户 REST API 集成测试：覆盖新增、查询、更新、删除与参数校验。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTest {

        private static final ParameterizedTypeReference<Map<String, Object>> JSON_MAP = new ParameterizedTypeReference<>() {
        };

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        @SuppressWarnings({ "unchecked", "null" })
        void fullUserLifecycle() {
                // 1. 新增用户
                Map<String, Object> createBody = new HashMap<>();
                createBody.put("name", "测试用户");
                createBody.put("email", "tester@example.com");
                createBody.put("phone", "13800000000");

                ResponseEntity<Map<String, Object>> created = restTemplate.exchange(
                                "/api/users", HttpMethod.POST, new HttpEntity<>(createBody), JSON_MAP);
                assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(created.getBody()).isNotNull();
                Map<String, Object> createdBody = created.getBody();
                assertThat(createdBody.get("code")).isEqualTo(0);

                Map<String, Object> createdUser = (Map<String, Object>) createdBody.get("data");
                assertThat(createdUser.get("name")).isEqualTo("测试用户");
                assertThat(createdUser.get("email")).isEqualTo("tester@example.com");

                long id = ((Number) createdUser.get("id")).longValue();

                // 2. 按 ID 查询
                ResponseEntity<Map<String, Object>> found = restTemplate.exchange(
                                "/api/users/" + id, HttpMethod.GET, null, JSON_MAP);
                assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
                Map<String, Object> foundBody = found.getBody();
                assertThat(foundBody).isNotNull();
                assertThat(foundBody.get("code")).isEqualTo(0);

                // 3. 查询列表
                ResponseEntity<Map<String, Object>> list = restTemplate.exchange(
                                "/api/users", HttpMethod.GET, null, JSON_MAP);
                assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
                Map<String, Object> listBody = list.getBody();
                assertThat(listBody).isNotNull();
                assertThat(listBody.get("data")).isNotNull();

                // 4. 更新用户
                Map<String, Object> updateBody = new HashMap<>();
                updateBody.put("name", "测试用户-改");
                updateBody.put("email", "tester-updated@example.com");
                updateBody.put("phone", "13900000000");

                ResponseEntity<Map<String, Object>> updated = restTemplate.exchange(
                                "/api/users/" + id, HttpMethod.PUT, new HttpEntity<>(updateBody), JSON_MAP);
                assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);

                ResponseEntity<Map<String, Object>> afterUpdate = restTemplate.exchange(
                                "/api/users/" + id, HttpMethod.GET, null, JSON_MAP);
                Map<String, Object> afterUpdateBody = afterUpdate.getBody();
                assertThat(afterUpdateBody).isNotNull();
                Map<String, Object> updatedUser = (Map<String, Object>) afterUpdateBody.get("data");
                assertThat(updatedUser.get("name")).isEqualTo("测试用户-改");

                // 5. 删除用户
                ResponseEntity<Map<String, Object>> deleted = restTemplate.exchange(
                                "/api/users/" + id, HttpMethod.DELETE, null, JSON_MAP);
                assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.OK);

                // 6. 删除后查询应返回 404
                ResponseEntity<Map<String, Object>> missing = restTemplate.exchange(
                                "/api/users/" + id, HttpMethod.GET, null, JSON_MAP);
                assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                Map<String, Object> missingBody = missing.getBody();
                assertThat(missingBody).isNotNull();
                assertThat(missingBody.get("code")).isEqualTo(404);
        }

        @Test
        @SuppressWarnings("null")
        void shouldReturn400WhenRequestIsInvalid() {
                Map<String, Object> invalidBody = new HashMap<>();
                invalidBody.put("name", "");
                invalidBody.put("email", "not-an-email");

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                                "/api/users", HttpMethod.POST, new HttpEntity<>(invalidBody), JSON_MAP);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                Map<String, Object> responseBody = response.getBody();
                assertThat(responseBody).isNotNull();
                assertThat(responseBody.get("code")).isEqualTo(400);
        }
}
