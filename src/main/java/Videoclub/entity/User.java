package Videoclub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name =  "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private LocalDate createdAt ;
    @Column(nullable = false)
    private LocalDate updatedAt ;
    @Column(nullable = false)
    private String createdBy ;
    @Column(nullable = false)
    private String updateBy;

    //relacion
    @OneToMany(mappedBy = "user")
    private List<Rental> rentals;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String normalized = (role != null) ? role.trim().toUpperCase() : "";
        return List.of(new SimpleGrantedAuthority("ROLE_" + normalized));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return "ACTIVE".equals(status); }
}