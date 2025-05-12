package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Artem Sergeevich", "Software Engineer"));
        save(new Candidate(0, "Smirnova Anastasiya Dmitrievna", "Marketing Manager"));
        save(new Candidate(0, "Kozlov Mikhail Igorevich", "Neurosurgeon"));
        save(new Candidate(0, "Novikova Ekaterina Pavlovna", "Art Curator"));
        save(new Candidate(0, "Fyodorov Aleksandr Vladimirovich", "Criminal Investigator"));
        save(new Candidate(0, "Morozova Olga Andreevna", "Climate Scientist"));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(), candidate.getDescription())) != null;
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
