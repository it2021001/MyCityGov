package gr.mycitygov.service;

import gr.mycitygov.model.Department;
import gr.mycitygov.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public Department create(String name) {
        String trimmed = name == null ? "" : name.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Department name is required");
        }
        if (departmentRepository.existsByName(trimmed)) {
            throw new IllegalArgumentException("Department already exists");
        }

        Department d = new Department();
        d.setName(trimmed);
        return departmentRepository.save(d);
    }

    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
}
