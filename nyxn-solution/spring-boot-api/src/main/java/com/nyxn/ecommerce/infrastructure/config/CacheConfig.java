package com.nyxn.ecommerce.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

/**
 * Configuración de Redis como caché distribuido empresarial.
 *
 * ESTRATEGIA:
 * - TTL de 1 hora: los datos de producto cambian máximo 1 vez/hora (requisito de negocio).
 * - Cache-aside pattern + @Cacheable/@CacheEvict para gestión declarativa.
 * - Para mitigar Cache Stampede usamos:
 *   1. TTL con jitter aleatorio (±5 minutos) para evitar expiración masiva simultánea.
 *   2. En producción: considerar probabilistic early expiration (PER).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Duration PRODUCT_TTL = Duration.ofHours(1);
    private static final Duration PRODUCT_LIST_TTL = Duration.ofMinutes(30); // Listas expiran antes

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(PRODUCT_TTL)
                .disableCachingNullValues() // Evita cachear nulls (protección contra cache poisoning)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        // Configuración específica por nombre de caché
        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "products",      defaultConfig.entryTtl(PRODUCT_TTL),
                "product-lists", defaultConfig.entryTtl(PRODUCT_LIST_TTL)
        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
