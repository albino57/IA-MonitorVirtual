package com.example.repository;

import com.example.domain.ConteudoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConteudoRepository extends JpaRepository<ConteudoEntity, Long> {

    // Faz a busca por similaridade usando a distância de cosseno do pgvector (<=>)
    @Query(value = "SELECT texto FROM apostila_conteudo ORDER BY embedding <=> cast(:embeddingAsText as vector) LIMIT :limite", nativeQuery = true)
    List<String> buscarContextoSemelhante(@Param("embeddingAsText") String embeddingAsText, @Param("limite") int limite);
}