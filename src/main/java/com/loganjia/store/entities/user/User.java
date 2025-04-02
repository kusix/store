package com.loganjia.store.entities.user;

import com.loganjia.store.entities.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="password")
    @ToString.Exclude
    private String password;

    @Column(name="email")
    private String email;

    @OneToMany(mappedBy = "user",cascade = CascadeType.PERSIST,orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<Address>();

    public void addAddress(Address address) {
        address.setUser(this);
        this.addresses.add(address);
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        address.setUser(null);
    }

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "users_tags"
            , joinColumns = @JoinColumn(name = "user_id")
            , inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<Tag>();

    public void addTag(String tagName) {
        var tag = new Tag(tagName);
        this.tags.add(tag);
        tag.getUsers().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getUsers().remove(this);
    }

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "wishlist"
            , joinColumns = @JoinColumn(name="user_id")
            , inverseJoinColumns = @JoinColumn(name="product_id")
    )
    @Builder.Default
    private Set<Product> wishlist = new HashSet<>();

    public void addWishlist(Product product) {
        this.wishlist.add(product);
    }

    public void removeWishlist(Product product) {
        this.wishlist.remove(product);
    }

//    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    private Profile profile;
//
//    public void addProfile(Profile profile) {
//        this.profile = profile;
//        profile.setUser(this);
//    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "email = " + email + ")";
    }
}
