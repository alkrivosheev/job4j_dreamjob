package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Artem Sergeevich", "Software Engineer", LocalDateTime.now(), 1));
        save(new Candidate(0, "Smirnova Anastasiya Dmitrievna", "Marketing Manager", LocalDateTime.now(), 2));
        save(new Candidate(0, "Kozlov Mikhail Igorevich", "Neurosurgeon", LocalDateTime.now(), 3));
        save(new Candidate(0, "Novikova Ekaterina Pavlovna", "Art Curator", LocalDateTime.now(), 1));
        save(new Candidate(0, "Fyodorov Aleksandr Vladimirovich", "Criminal Investigator", LocalDateTime.now(), 2));
        save(new Candidate(0, "Morozova Olga Andreevna", "Climate Scientist", LocalDateTime.now(), 3));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> {
            return new Candidate(
                    oldCandidate.getId(), candidate.getName(), candidate.getDescription(),
                    candidate.getCreationDate(), candidate.getCityId()); }) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
