package com.example.iotmanagementdevice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(nullable = false, unique = true)
    val name: String?,
    @Column(nullable = false, unique = true)
    val email: String?,
    @Column(nullable = false, unique = true)
    val phoneNumber: String?,
    @Column(nullable = false)
    val userPassword: String?,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role>?,
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val devices: MutableList<Device>?
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority?>? {
        return roles
    }

    override fun getPassword(): String? {
        return userPassword
    }

    override fun getUsername(): String? {
        return name
    }

    override fun toString(): String {
        return "User(id=$id, name=$name, email=$email, phoneNumber=$phoneNumber)"
    }
}
