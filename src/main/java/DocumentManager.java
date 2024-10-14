import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    Map<String, Document> documentStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {

        if(document.id == null || document.id.isEmpty()) {
           document.id = generateRandomId();
        }

        if (validateId(document.id)) {
            documentStorage.put(document.id, document);
        }

        return document;
    }

    private boolean validateId(String documentId) {
        if (documentStorage.containsKey(documentId)) {
            System.out.println("The ID " + documentId + " is already taken. Please use a different ID.");
            return false;
        }
        return true;
    }

    private String generateRandomId() {
        String randomId;
        do {
            randomId = String.valueOf((int) (Math.random() * 1000));
        } while (!validateId(randomId));
        return randomId;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        List<Document> documentList = new ArrayList<>();

        for (Document doc : documentStorage.values()) {
            if (matchesRequest(doc, request)) {
                documentList.add(doc);
            }
        }
        return documentList.isEmpty() ? Collections.emptyList() : documentList;
    }

    private boolean matchesRequest(Document document, SearchRequest request) {
        if (request.titlePrefixes != null && !request.titlePrefixes.isEmpty()) {
            boolean titleMatch = request.titlePrefixes.stream()
                    .anyMatch(prefix -> document.title.startsWith(prefix));
            if (!titleMatch) return false;
        }

        if (request.containsContents != null && !request.containsContents.isEmpty()) {
            boolean contentMatch = request.containsContents.stream()
                    .anyMatch(content -> document.content.contains(content));
            if (!contentMatch) return false;
        }

        if (request.authorIds != null && !request.authorIds.isEmpty()) {
            boolean authorMatch = request.authorIds.stream()
                    .anyMatch(authorId -> document.author != null && document.author.id.equals(authorId));
            if (!authorMatch) return false;
        }

        if (request.createdFrom != null && document.created.isBefore(request.createdFrom)) {
            return false;
        }

        if (request.createdTo != null && document.created.isAfter(request.createdTo)) {
            return false;
        }

        return true;
    }




    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }



    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

}