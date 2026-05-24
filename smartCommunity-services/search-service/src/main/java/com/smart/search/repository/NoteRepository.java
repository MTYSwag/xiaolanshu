package com.smart.search.repository;

import com.smart.search.document.NoteDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends ElasticsearchRepository<NoteDocument, Long> {
}
