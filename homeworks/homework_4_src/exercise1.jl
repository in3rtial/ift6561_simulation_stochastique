# implement the rng

type mrg
  state::Vector{Int}
  coefficients::Vector{Int}
  k::Int
  m::Int

  # constructor with some checks
  function mrg(state::Vector{Int}, coefficients::Vector{Int}, m::Int)
    assert (length(state) == length(coefficients))
    assert (coefficients != int(zeros(length(coefficients))))
    new(state, coefficients, length(coefficients), m)
    end

end


type mrg2
  state::Vector{Int}
  jump_matrix::Matrix{Int}
  k::Int
  m::Int
  function mrg2(state::Vector{Int}, coefficients::Vector{Int}, m::Int)
    # constructor
    assert (length(state) == length(coefficients))
    assert (coefficients != int(zeros(length(coefficients))))
    # create the jump matrix
    jump_matrix::Matrix{Int} = int(eye(length(state) - 1))
    jump_matrix = hcat(int(zeros(length(state)-1)), jump_matrix)
    jump_matrix = vcat(jump_matrix, transpose(coefficients))
    new(state, jump_matrix, length(state), m)
  end
end


function next(prng::mrg2)
  a = prng.state * prng.jump_matrix
  prng.state = a
  a
end


function next(prng::mrg)
  # jump one state
  xnp1 = mod(dot(prng.state, prng.coefficients), prng.m)
  shift!(push!(prng.state, xnp1))
  println(xnp1)
  xnp1 / prng.m
end


a = [1 2; 3 4]
b = [1 0; 0 1]
