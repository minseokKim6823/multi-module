package practice.moduleuser.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/admin/caches")
public class CacheInspectorController {
    private final CacheManager cacheManager;

    public CacheInspectorController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping
    public List<Map<String, Object>> listCaches() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache springCache = cacheManager.getCache(name);
            long size = -1L;
            if (springCache instanceof CaffeineCache caffeineCache) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                size = nativeCache.estimatedSize();
            }
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", name);
            info.put("size", size);
            result.add(info);
        }
        return result;
    }

    @GetMapping("/{name}")
    public Map<String, Object> cacheDetails(@PathVariable String name,
                                            @RequestParam(defaultValue = "false") boolean includeValues) {
        org.springframework.cache.Cache springCache = cacheManager.getCache(name);
        if (springCache == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache not found: " + name);
        }
        if (!(springCache instanceof CaffeineCache caffeineCache)) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                    "Unsupported cache type: " + springCache.getClass().getName());
        }

        Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        Map<Object, Object> map = nativeCache.asMap();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("name", name);
        resp.put("size", nativeCache.estimatedSize());
        resp.put("keys", new ArrayList<>(map.keySet()));

        if (includeValues) {
            if ("users".equals(name)) {
                // Sanitize user entities to avoid exposing sensitive fields (e.g., password)
                List<Map<String, Object>> values = new ArrayList<>();
                for (Object v : map.values()) {
                    try {
                        Class<?> userClass = Class.forName("practice.moduleuser.user.User");
                        if (userClass.isInstance(v)) {
                            Object u = v;
                            Map<String, Object> mv = new LinkedHashMap<>();
                            mv.put("id", userClass.getMethod("getId").invoke(u));
                            mv.put("email", userClass.getMethod("getEmail").invoke(u));
                            mv.put("name", userClass.getMethod("getName").invoke(u));
                            mv.put("role", userClass.getMethod("getRole").invoke(u));
                            mv.put("deletedAt", userClass.getMethod("getDeletedAt").invoke(u));
                            values.add(mv);
                        } else {
                            values.add(Collections.singletonMap("type", v == null ? "null" : v.getClass().getName()));
                        }
                    } catch (Exception e) {
                        values.add(Collections.singletonMap("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
                    }
                }
                resp.put("values", values);
            } else {
                // For non-user caches, avoid dumping full objects; show class types only
                List<String> values = map.values().stream()
                        .map(v -> v == null ? "null" : v.getClass().getName())
                        .toList();
                resp.put("values", values);
            }
        }
        return resp;
    }
}

